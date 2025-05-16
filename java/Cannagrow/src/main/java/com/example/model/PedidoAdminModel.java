package com.example.model;

import com.example.model.PedidoModel.Pedido;
import com.example.model.PedidoModel.DetallePedido;
import com.example.model.PedidoModel.EstadoPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoAdminModel {

    /**
     * Construye un objeto {@link Pedido} a partir de un ResultSet de una consulta SQL.
     *
     * @param rs ResultSet con los datos del pedido.
     * @return Objeto {@link Pedido} construido.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */

    private static Pedido construirPedidoDesdeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Date fecha = rs.getTimestamp("fecha");
        float total = rs.getFloat("total");
        String estadoStr = rs.getString("estado");
        int clienteId = rs.getInt("cliente_id");
        String vehiculoMatricula = rs.getString("vehiculo_matricula");

        EstadoPedido estado = null;
        for (EstadoPedido e : EstadoPedido.values()) {
            if (e.getEstado().equalsIgnoreCase(estadoStr)) {
                estado = e;
                break;
            }
        }

        Pedido pedido = new Pedido(fecha, total, estado, clienteId, vehiculoMatricula);
        pedido.setId(id);
        return pedido;
    }

    /**
     * Obtiene todos los pedidos paginados según el offset y el límite especificados.
     *
     * @param offset Desplazamiento desde donde empezar a obtener los pedidos.
     * @param limit  Número máximo de pedidos a obtener.
     * @return Lista de objetos {@link Pedido}.
     */

    public static List<Pedido> obtenerTodosPedidos(int offset, int limit) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT id, fecha, total, estado, cliente_id, vehiculo_matricula " +
                "FROM Pedido ORDER BY fecha DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date fecha = rs.getTimestamp("fecha");
                    float total = rs.getFloat("total");
                    String estadoStr = rs.getString("estado");
                    int clienteId = rs.getInt("cliente_id");
                    String vehiculoMatricula = rs.getString("vehiculo_matricula");

                    EstadoPedido estado = EstadoPedido.fromString(estadoStr); // método que tú debes crear

                    Pedido pedido = new Pedido(fecha, total, estado, clienteId, vehiculoMatricula);
                    pedido.setId(id);
                    pedidos.add(pedido);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los pedidos: " + e.getMessage());
            e.printStackTrace();
        }

        return pedidos;
    }




    /**
     * Obtiene una lista de pedidos filtrados por estado, de forma paginada.
     *
     * @param estado Estado del pedido a filtrar.
     * @param pagina Número de página actual.
     * @param limite Número de pedidos por página.
     * @return Lista de pedidos con el estado especificado.
     */

    public static List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado, int pagina, int limite) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE estado = ? ORDER BY fecha DESC LIMIT ? OFFSET ?";

        int offset = (pagina - 1) * limite;

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado.getEstado());
            ps.setInt(2, limite);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(construirPedidoDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos por estado: " + e.getMessage());
        }

        return pedidos;
    }

    /**
     * Actualiza el estado de un pedido específico.
     *
     * @param pedidoId    ID del pedido a actualizar.
     * @param nuevoEstado Nuevo estado que se asignará al pedido.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */

    public static boolean actualizarEstadoPedido(int pedidoId, EstadoPedido nuevoEstado) {
        String sql = "UPDATE Pedido SET estado = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado.getEstado());
            ps.setInt(2, pedidoId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Devuelve el stock de los productos de un pedido cancelado.
     *
     * @param pedidoId ID del pedido cancelado.
     */

    public static void devolverStockPedidoCancelado(int pedidoId) {
        String sqlDetalle = "SELECT producto_id, cantidad FROM ItemPedido WHERE pedido_id = ?";
        String sqlUpdateStock = "UPDATE Producto SET stock = stock + ? WHERE id = ?";

        try (Connection conn = DBUtil.getConexion()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateStock)) {

                psDetalle.setInt(1, pedidoId);
                try (ResultSet rs = psDetalle.executeQuery()) {
                    while (rs.next()) {
                        int productoId = rs.getInt("producto_id");
                        int cantidad = rs.getInt("cantidad");

                        psUpdate.setInt(1, cantidad);
                        psUpdate.setInt(2, productoId);
                        psUpdate.executeUpdate();
                    }
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error al devolver stock: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión al devolver stock: " + e.getMessage());
        }
    }

    /**
     * Obtiene el nombre del cliente a partir de su ID.
     *
     * @param clienteId ID del cliente.
     * @return Nombre del cliente si se encuentra, o "Cliente desconocido" en caso contrario.
     */

    public static String obtenerNombreClientePorId(int clienteId) {
        String nombre = "Cliente desconocido";
        String sql = "SELECT nombre FROM Cliente WHERE id = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombre = rs.getString("nombre");

                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener nombre del cliente: " + e.getMessage());
        }

        return nombre;
    }

    /**
     * Obtiene una lista de empleados disponibles, incluyendo aquellos con rol "Empleado" o "Repartidor".
     *
     * @return Lista de objetos {@link UsuarioModel} representando empleados disponibles.
     */

    public static List<UsuarioModel> obtenerEmpleadosDisponibles() {
        List<UsuarioModel> empleados = new ArrayList<>();
        String sql = "SELECT * FROM Empleado WHERE rol IN ('Empleado', 'Repartidor')";

        try (Connection conn = DBUtil.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                UsuarioModel empleado = new UsuarioModel();
                empleado.setId(rs.getInt("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setEmail(rs.getString("email"));
                empleado.setRol(rs.getString("rol"));
                try {
                    empleado.setSalario(rs.getDouble("salario"));
                } catch (SQLException e) {
                    empleado.setSalario(0.0);
                }

                empleados.add(empleado);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener empleados disponibles: " + e.getMessage());
        }

        return empleados;
    }

    /**
     * Asigna un empleado a un pedido determinado.
     *
     * @param pedidoId   ID del pedido.
     * @param empleadoId ID del empleado que se asignará.
     * @return {@code true} si se asignó correctamente, {@code false} si ocurrió un error.
     */

    public static boolean asignarEmpleadoAPedido(int pedidoId, int empleadoId) {
        String sql = "UPDATE Pedido SET empleado_id = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empleadoId);
            ps.setInt(2, pedidoId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al asignar empleado al pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los pedidos asignados a un empleado específico.
     *
     * @param empleadoId ID del empleado.
     * @return Lista de objetos {@link Pedido} asignados al empleado.
     */

    public static List<Pedido> obtenerPedidosPorEmpleado(int empleadoId) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE empleado_id = ? ORDER BY fecha DESC";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empleadoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(construirPedidoDesdeResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos del empleado: " + e.getMessage());
        }

        return pedidos;
    }


}
