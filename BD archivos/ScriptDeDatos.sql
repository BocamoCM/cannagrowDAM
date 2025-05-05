-- Plantilla

-- PRODUCTO
-- INSERT INTO Producto (nombre, tipo, contenidoTHC, contenidoCBD, precio, stock)
-- VALUES ('NombreProducto', 'Flor', 18.5, 0.2, 25.99, 100);

-- CLIENTE
-- INSERT INTO Cliente (nombre, fechaNacimiento, email, direccion)
-- VALUES ('Juan Pérez', '1990-03-25', 'juan.perez@email.com', 'Calle Falsa 123, Ciudad');

-- EMPLEADO
-- INSERT INTO Empleado (nombre, rol, email, salario)
-- VALUES ('María Gómez', 'Vendedor', 'maria.gomez@email.com', 2500.00);

-- PEDIDO
-- INSERT INTO Pedido (fecha, total, estado, cliente_id)
-- VALUES ('2025-05-01', 0.0, 'Pendiente', 1); -- Asumiendo que el cliente con id=1 ya existe

-- ITEMPEDIDO
-- INSERT INTO ItemPedido (pedido_id, producto_id, cantidad)
-- VALUES (1, 1, 2); -- Asume que pedido_id=1 y producto_id=1 existen

-- REGULACION
-- INSERT INTO Regulacion (descripcion)
-- VALUES ('Producto no debe exceder 20% de THC para venta libre.');

-- DATOS EMPLEADOS
INSERT INTO Empleado (nombre, rol, email, salario) 
VALUES ('Joaquin Tena', 'Gerente', 'joaquin_tena@email.com', 4400.00),
('Borja Carreres', 'Gerente', 'borja_carreres@email.com', 6400.00),
('Andreu Rosell', 'Gerente', 'andreu_rosell@email.com', 10000.00),
('Nacho Piles', 'Cultivador', 'nacho_piles@email.com', 1400.00),
('Mario mas', 'Vendedor', 'mario_mas@email.com', 1850.00),
('Lucía Vega', 'Vendedor', 'lucia.vega@email.com', 2100.00),
('Carlos Ruiz', 'Vendedor', 'carlos.ruiz@email.com', 1300.00),
('Ana Torres', 'Vendedor', 'ana.torres@email.com', 1950.00),
('José Morales', 'Vendedor', 'jose.morales@email.com', 1400.00),
('Elena Bravo', 'Vendedor', 'elena.bravo@email.com', 1750.00),
('Luis Cano', 'Vendedor', 'luis.cano@email.com', 2200.00),
('Marta Gil', 'Vendedor', 'marta.gil@email.com', 1600.00),
('Pablo Díaz', 'Vendedor', 'pablo.diaz@email.com', 1200.00),
('Sandra León', 'Vendedor', 'sandra.leon@email.com', 2050.00),
('Jorge Paredes', 'Cultivador', 'jorge.paredes@email.com', 1450.00),
('Claudia Rivas', 'Cultivador', 'claudia.rivas@email.com', 1000.00),
('Iván Soto', 'Cultivador', 'ivan.soto@email.com', 1300.00),
('Verónica Castro', 'Cultivador', 'veronica.castro@email.com', 1100.00),
('Pedro Navas', 'Cultivador', 'pedro.navas@email.com', 1200.00),
('Camila Durán', 'Cultivador', 'camila.duran@email.com', 950.00),
('Andrés Muñoz', 'Cultivador', 'andres.munoz@email.com', 1250.00),
('Raquel Romero', 'Cultivador', 'raquel.romero@email.com', 1350.00),
('David Salas', 'Cultivador', 'david.salas@email.com', 1150.00),
('Sofía Méndez', 'Cultivador', 'sofia.mendez@email.com', 900.00),
('Esteban Lara', 'Cultivador', 'esteban.lara@email.com', 1420.00),
('Paula Guzmán', 'Cultivador', 'paula.guzman@email.com', 1280.00),
('Héctor Arias', 'Cultivador', 'hector.arias@email.com', 1490.00),
('Laura Peña', 'Cultivador', 'laura.pena@email.com', 1380.00),
('Rafael Valdés', 'Cultivador', 'rafael.valdes@email.com', 920.00),
('Natalia Prado', 'Cultivador', 'natalia.prado@email.com', 1050.00),
('Bruno Ortiz', 'Cultivador', 'bruno.ortiz@email.com', 1320.00),
('Valentina Mora', 'Cultivador', 'valentina.mora@email.com', 1430.00),
('Diego Cordero', 'Cultivador', 'diego.cordero@email.com', 1170.00),
('Isabel Rico', 'Cultivador', 'isabel.rico@email.com', 1250.00),
('Gabriel Pinto', 'Cultivador', 'gabriel.pinto@email.com', 1400.00),
('Emma Molina', 'Cultivador', 'emma.molina@email.com', 1180.00);







