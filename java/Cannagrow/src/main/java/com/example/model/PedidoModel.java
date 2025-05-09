package com.example.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoModel {

    public enum EstadoPedido {
        PENDIENTE("Pendiente"),
        ENVIADO("Enviado"),
        ENTREGADO("Entregado"),
        CANCELADO("Cancelado");

        private final String estado;
        EstadoPedido(String estado) {
            this.estado = estado;
        }
        public String getEstado() {
            return estado;
        }
    }

    public static class Pedido {
        private int id;
        private Date fecha;
        private float total;
        private EstadoPedido estado;
        private int clienteId;
        private int empleadoId;
        private String vehiculoMatricula;
        private boolean notificado;
        private List<DetallePedido> detalles;

        public Pedido(Date fecha, float total, EstadoPedido estado, int clienteId, String vehiculoMatricula) {
            this.fecha = fecha;
            this.total = total;
            this.estado = estado;
            this.clienteId = clienteId;
            this.vehiculoMatricula = vehiculoMatricula;
            this.notificado = false;
            this.detalles = new ArrayList<>();
        }

        public void agregarDetalle(DetallePedido d) {
            detalles.add(d);
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public Date getFecha() { return fecha; }
        public float getTotal() { return total; }
        public EstadoPedido getEstado() { return estado; }
        public int getClienteId() { return clienteId; }
        public String getVehiculoMatricula() { return vehiculoMatricula; }
        public boolean isNotificado() { return notificado; }
        public List<DetallePedido> getDetalles() { return detalles; }
    }

    public static class DetallePedido {
        private int productoId;
        private int cantidad;
        private float precioUnitario;
        private float subtotal;
        private Producto producto;

        public DetallePedido(int productoId, int cantidad, float precioUnitario) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = precioUnitario * cantidad;
        }

        public int getProductoId() { return productoId; }
        public int getCantidad() { return cantidad; }
        public float getPrecioUnitario() { return precioUnitario; }
        public float getSubtotal() { return subtotal; }
        public Producto getProducto() { return producto; }
        public void setProducto(Producto producto) { this.producto = producto; }
    }

    public static int crearPedido(Pedido pedido) {
        int pedidoId = -1;
        Connection conn = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;

        try {
            conn = DBUtil.getConexion();
            conn.setAutoCommit(false); // inicio de transacción

            // Insertar pedido
            String sqlPedido = "INSERT INTO Pedido (fecha, total, estado, cliente_id, vehiculo_matricula, notificado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setDate(1, new java.sql.Date(pedido.getFecha().getTime()));
            psPedido.setFloat(2, pedido.getTotal());
            psPedido.setString(3, pedido.getEstado().getEstado());
            psPedido.setInt(4, pedido.getClienteId());
            psPedido.setString(5, pedido.getVehiculoMatricula());
            psPedido.setBoolean(6, pedido.isNotificado());
            psPedido.executeUpdate();

            ResultSet rs = psPedido.getGeneratedKeys();
            if (rs.next()) {
                pedidoId = rs.getInt(1);
                pedido.setId(pedidoId);
            } else {
                conn.rollback();
                return -1;
            }

            // Insertar detalles
            String sqlDetalle = "INSERT INTO ItemPedido (pedido_id, producto_id, cantidad) VALUES (?, ?, ?)";
            psDetalle = conn.prepareStatement(sqlDetalle);

            for (DetallePedido detalle : pedido.getDetalles()) {
                psDetalle.setInt(1, pedidoId);
                psDetalle.setInt(2, detalle.getProductoId());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.addBatch();

                // Actualizar stock
                actualizarStock(conn, detalle.getProductoId(), detalle.getCantidad());
            }

            psDetalle.executeBatch();
            conn.commit(); // todo correcto

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (psPedido != null) psPedido.close();
                if (psDetalle != null) psDetalle.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        return pedidoId;
    }

    private static void actualizarStock(Connection conn, int productoId, int cantidad) throws SQLException {
        String sql = "UPDATE Producto SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, productoId);
            ps.executeUpdate();
        }
    }

    public static int obtenerClienteIdPorUsuario(int usuarioId) {
        String sql = "SELECT id FROM Cliente WHERE id = ?";
        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

        } catch (SQLException e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
        }
        return -1;
    }

    public static List<String> obtenerMatriculasVehiculos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT matricula FROM Vehiculo";
        try (Connection conn = DBUtil.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(rs.getString("matricula"));
        } catch (SQLException e) {
            System.err.println("Error al obtener vehículos: " + e.getMessage());
        }
        return lista;
    }
}
