package com.example.model;

import com.example.model.PedidoModel.Pedido;
import com.example.model.PedidoModel.DetallePedido;
import com.example.model.PedidoModel.EstadoPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoAdminModel {

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





    public static List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE estado = ? ORDER BY fecha DESC";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado.getEstado());

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
