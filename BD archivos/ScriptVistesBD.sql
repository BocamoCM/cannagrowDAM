create view CantidadPedidosEmplado as
SELECT e.nombre, COUNT(p.id) AS total_pedidos FROM Empleado e
JOIN Pedido p ON e.id = p.empleado_id
GROUP BY e.nombre;
