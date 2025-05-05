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

