create view CantidadPedidosEmplado as
SELECT e.nombre, COUNT(p.id) AS total_pedidos FROM Empleado e
JOIN Pedido p ON e.id = p.empleado_id
GROUP BY e.nombre;

create view PedidoEmpleadoClienteProducto as
select e.nombre, c.nombre, pr.nombre, pe.id from Pedido pe
join Empleado e on pe.empleado_id = e.id
join Cliente c on pe.cliente_id = c.id
join Producto pr on pe.empleado_id = pr.id
join ItemPedido ip on pe.id = pedido_id;