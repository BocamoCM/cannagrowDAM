DELIMITER $$

-- Primero, eliminamos el trigger si existe.
DROP TRIGGER IF EXISTS actualiza_pedido_trigger;

-- Creamos el trigger AFTER INSERT para la tabla Pedido.
CREATE TRIGGER actualiza_pedido_trigger
AFTER INSERT ON Pedido
FOR EACH ROW
BEGIN
    -- Aquí puedes poner cualquier lógica, pero NO actualices la misma tabla "Pedido".
    -- Si necesitas hacer una actualización, por ejemplo en una tabla de logs o una tabla auxiliar, hazlo aquí.
    -- Un ejemplo podría ser agregar un registro a una tabla de logs:
    INSERT INTO PedidoLog (pedido_id, fecha_accion, accion)
    VALUES (NEW.id, NOW(), 'Pedido insertado');
END $$

DELIMITER ;