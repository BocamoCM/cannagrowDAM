DELIMITER $$

-- Primero, eliminamos el trigger si existe.
DROP TRIGGER IF EXISTS actualiza_pedido_trigger;

-- Creamos el trigger AFTER INSERT para la tabla Pedido.
CREATE TRIGGER actualiza_pedido_trigger
AFTER INSERT ON Pedido
FOR EACH ROW
BEGIN
    -- Insertamos un registro en la tabla de logs.
    INSERT INTO PedidoLog (pedido_id, fecha_accion, accion)
    VALUES (NEW.id, NOW(), 'Pedido insertado');
    
    -- Si es necesario, puedes hacer otras acciones aquí que no impliquen modificar la tabla Pedido.
END $$

DELIMITER ;
