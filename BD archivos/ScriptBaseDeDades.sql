-- Crear la base de datos
CREATE DATABASE CannaGrowBD;
USE CannaGrowBD;

-- Tabla Producto
CREATE TABLE Producto (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(250) NOT NULL,
    tipo ENUM('Flor','Aceite','Comestible','Extracto', 'Semilla','Artilujos','Paquete','Ropa', 'Recurso','Bebida','Cosmético','Mascotas') NOT NULL,
    contenidoTHC FLOAT NOT NULL,
    contenidoCBD FLOAT NOT NULL,
    precio FLOAT NOT NULL,
    stock INT NOT NULL,
    imagen_producto VARCHAR(255)
);

-- Tabla Cliente
CREATE TABLE Cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    fechaNacimiento DATE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    contrasena_hash CHAR(60) NOT NULL,
    usuario_id int,
    discordid varchar(255),
    fotoPerfilUrl VARCHAR(255)
);

-- Tabla Empleado
CREATE TABLE Empleado (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('Vendedor', 'Cultivador', 'Gerente', 'Repartidor') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    salario FLOAT NOT NULL,
    contrasena_hash CHAR(60) NOT NULL,
    FotoPerfilUrl VARCHAR(255)
);

-- Tabla Vehiculo (debe ir antes que Pedido)
CREATE TABLE Vehiculo (
    matricula VARCHAR(15) PRIMARY KEY,
    color VARCHAR(50) NOT NULL,
    conductor_id INT NOT NULL,
    marca VARCHAR(100) NOT NULL,
    estado ENUM('Activo', 'En mantenimiento', 'Fuera de servicio') NOT NULL,
    kilometros_totales INT NOT NULL,
    consumo_100km FLOAT NOT NULL,
    cv_motor INT NOT NULL,
    FOREIGN KEY (conductor_id) REFERENCES Empleado(id)
);

-- Tabla Pedido
CREATE TABLE Pedido (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    total FLOAT DEFAULT 0,
    estado ENUM('Pendiente', 'Enviado', 'Entregado', 'Cancelado') NOT NULL,
    cliente_id INT,
    empleado_id INT,
    vehiculo_matricula VARCHAR(15) NOT NULL,
    notificado BOOLEAN default false,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id),
    FOREIGN KEY (empleado_id) REFERENCES Empleado(id),
    FOREIGN KEY (vehiculo_matricula) REFERENCES Vehiculo(matricula)
);

-- Tabla ItemPedido
CREATE TABLE ItemPedido (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES Pedido(id),
    FOREIGN KEY (producto_id) REFERENCES Producto(id)
);

-- Tabla Regulacion
CREATE TABLE Regulacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    descripcion TEXT NOT NULL
);

-- Tabla PedidoLog
CREATE TABLE PedidoLog (
	id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id int null,
    fecha_accion datetime not null,
    accion varchar(255) not null
);

-- Tabla Sesion
CREATE TABLE Sesion (
	id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id int,
    inicio timestamp,
    ip varchar(45),
    dispositivo varchar(100),
    FOREIGN KEY (usuario_id) REFERENCES Cliente(id)
);

-- Tabla SesionActiva
CREATE TABLE SesionActiva (
	id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id int,
    tipo_usuario enum('Cliente', 'Empleado'),
    nombre_usuario varchar(100),
    inicio_sesion timestamp,
    fin_sesion timestamp,
    activa tinyint(1)
);

-- Tabla Reportes
CREATE TABLE reportes(
	id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id int,
    empleado_id int null,
    descripcion varchar(500) not null,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id),
    FOREIGN KEY (empleado_id) REFERENCES Empleado(id)
);