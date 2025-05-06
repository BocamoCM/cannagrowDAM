-- Crear la base de datos
CREATE DATABASE CannabisStore;
USE CannabisStore;

-- Tabla Producto
CREATE TABLE Producto (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('Flor', 'Aceite', 'Comestible', 'Extracto') NOT NULL,
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
    imagen_cliente VARCHAR(255)
);

-- Tabla Empleado
CREATE TABLE Empleado (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('Vendedor', 'Cultivador', 'Gerente') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    salario FLOAT NOT NULL,
    contrasena_hash CHAR(60) NOT NULL,
    imagen_empleado VARCHAR(255)
);

-- Tabla Pedido
CREATE TABLE Pedido (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    total FLOAT DEFAULT 0,
    estado ENUM('Pendiente', 'Enviado', 'Entregado', 'Cancelado') NOT NULL,
    cliente_id INT,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id)
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