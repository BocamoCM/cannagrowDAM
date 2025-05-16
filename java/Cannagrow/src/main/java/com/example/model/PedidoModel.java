package com.example.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase que gestiona la lógica de los pedidos, incluyendo la creación, obtención y detalle de los pedidos.
 */
public class PedidoModel {

    /**
     * Enumeración que representa los posibles estados de un pedido.
     */
    public enum EstadoPedido {
        PENDIENTE("Pendiente"),
        ENVIADO("Enviado"),
        ENTREGADO("Entregado"),
        CANCELADO("Cancelado");

        private final String estado;

        /**
         * Constructor del estado del pedido.
         * @param estado Nombre del estado.
         */
        EstadoPedido(String estado) {
            this.estado = estado;
        }

        /**
         * Obtiene el nombre del estado.
         * @return Estado en formato String.
         */
        public String getEstado() {
            return estado;
        }

        /**
         * Devuelve una instancia de EstadoPedido a partir de una cadena.
         * @param estadoStr Cadena del estado.
         * @return Instancia correspondiente de EstadoPedido, o null si no coincide.
         */
        public static EstadoPedido fromString(String estadoStr) {
            for (EstadoPedido estado : EstadoPedido.values()) {
                if (estado.getEstado().equalsIgnoreCase(estadoStr)) {
                    return estado;
                }
            }
            return null;
        }
    }

    /**
     * Clase que representa un pedido realizado por un cliente.
     */
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

        /**
         * Constructor de un pedido.
         * @param fecha Fecha del pedido.
         * @param total Total del pedido.
         * @param estado Estado actual del pedido.
         * @param clienteId ID del cliente.
         * @param vehiculoMatricula Matrícula del vehículo asociado.
         */
        public Pedido(Date fecha, float total, EstadoPedido estado, int clienteId, String vehiculoMatricula) {
            this.fecha = fecha;
            this.total = total;
            this.estado = estado;
            this.clienteId = clienteId;
            this.vehiculoMatricula = vehiculoMatricula;
            this.notificado = false;
            this.detalles = new ArrayList<>();
        }

        /**
         * Agrega un detalle al pedido.
         * @param d Detalle del pedido.
         */
        public void agregarDetalle(DetallePedido d) {
            detalles.add(d);
        }

        // Getters y Setters con Javadoc omitido por brevedad, se pueden documentar si lo deseas.
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public Date getFecha() { return fecha; }
        public float getTotal() { return total; }
        public EstadoPedido getEstado() { return estado; }
        public int getClienteId() { return clienteId; }
        public String getVehiculoMatricula() { return vehiculoMatricula; }
        public boolean isNotificado() { return notificado; }
        public List<DetallePedido> getDetalles() { return detalles; }
        public void setNotificado(boolean notificado) { this.notificado = notificado; }
        public void setEmpleadoId(int empleadoId) { this.empleadoId = empleadoId; }
        public int getEmpleadoId() { return empleadoId; }
        public void setEstado(EstadoPedido estado) { this.estado = estado; }
    }

    /**
     * Clase que representa un detalle individual de un pedido (producto, cantidad, subtotal).
     */
    public static class DetallePedido {
        private int productoId;
        private int cantidad;
        private float precioUnitario;
        private float subtotal;
        private Producto producto;

        /**
         * Constructor del detalle de un pedido.
         * @param productoId ID del producto.
         * @param cantidad Cantidad del producto.
         * @param precioUnitario Precio unitario del producto.
         */
        public DetallePedido(int productoId, int cantidad, float precioUnitario) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = precioUnitario * cantidad;
        }

        // Getters y Setters con Javadoc omitido por brevedad.
        public int getProductoId() { return productoId; }
        public int getCantidad() { return cantidad; }
        public float getPrecioUnitario() { return precioUnitario; }
        public float getSubtotal() { return subtotal; }
        public Producto getProducto() { return producto; }
        public void setProducto(Producto producto) { this.producto = producto; }
    }

    /**
     * Crea un nuevo pedido y lo guarda en la base de datos.
     * También guarda sus detalles y actualiza el stock de productos.
     * @param pedido Pedido a crear.
     * @return ID del nuevo pedido o -1 si falla.
     */
    public static int crearPedido(Pedido pedido) {
        // (Javadoc general del método incluido en el encabezado. Comentarios inline en el cuerpo si se desea.)
        int pedidoId = -1;
        Connection conn = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;

        try {
            conn = DBUtil.getConexion();
            conn.setAutoCommit(false);

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

            String sqlDetalle = "INSERT INTO ItemPedido (pedido_id, producto_id, cantidad) VALUES (?, ?, ?)";
            psDetalle = conn.prepareStatement(sqlDetalle);

            for (DetallePedido detalle : pedido.getDetalles()) {
                psDetalle.setInt(1, pedidoId);
                psDetalle.setInt(2, detalle.getProductoId());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.addBatch();
                actualizarStock(conn, detalle.getProductoId(), detalle.getCantidad());
            }

            psDetalle.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            System.err.println("Error al crear pedido: " + e.getMessage());
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

    /**
     * Actualiza el stock de un producto en la base de datos.
     * @param conn Conexión activa a la base de datos.
     * @param productoId ID del producto.
     * @param cantidad Cantidad a restar del stock.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    private static void actualizarStock(Connection conn, int productoId, int cantidad) throws SQLException {
        String sql = "UPDATE Producto SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, productoId);
            ps.executeUpdate();
        }
    }

    /**
     * Obtiene el ID del cliente a partir de su ID de usuario.
     * @param usuarioId ID del usuario.
     * @return ID del cliente o -1 si no se encuentra.
     */
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

    /**
     * Obtiene una lista con las matrículas de todos los vehículos.
     * @return Lista de matrículas.
     */
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

    /**
     * Obtiene todos los pedidos de un cliente específico
     * @param clienteId ID del cliente
     * @return Lista de pedidos del cliente
     */
    public static List<Pedido> obtenerPedidosPorCliente(int clienteId) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE cliente_id = ? ORDER BY fecha DESC";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date fecha = rs.getTimestamp("fecha");
                    float total = rs.getFloat("total");
                    String estadoStr = rs.getString("estado");
                    String vehiculoMatricula = rs.getString("vehiculo_matricula");
                    boolean notificado = rs.getBoolean("notificado");
                    int empleadoId = rs.getInt("empleado_id");

                    EstadoPedido estado = null;
                    for (EstadoPedido e : EstadoPedido.values()) {
                        if (e.getEstado().equals(estadoStr)) {
                            estado = e;
                            break;
                        }
                    }

                    Pedido pedido = new Pedido(fecha, total, estado, clienteId, vehiculoMatricula);
                    pedido.setId(id);

                    pedidos.add(pedido);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos del cliente: " + e.getMessage());
            e.printStackTrace();
        }

        return pedidos;
    }

    /**
     * Obtiene los pedidos de un cliente filtrados por estado
     * @param clienteId ID del cliente
     * @param estado Estado del pedido a filtrar
     * @return Lista de pedidos filtrados
     */
    public static List<Pedido> obtenerPedidosPorClienteYEstado(int clienteId, EstadoPedido estado) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE cliente_id = ? AND estado = ? ORDER BY fecha DESC";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ps.setString(2, estado.getEstado());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date fecha = rs.getTimestamp("fecha");
                    float total = rs.getFloat("total");
                    String vehiculoMatricula = rs.getString("vehiculo_matricula");
                    boolean notificado = rs.getBoolean("notificado");
                    int empleadoId = rs.getInt("empleado_id");

                    Pedido pedido = new Pedido(fecha, total, estado, clienteId, vehiculoMatricula);
                    pedido.setId(id);

                    pedidos.add(pedido);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos del cliente por estado: " + e.getMessage());
            e.printStackTrace();
        }

        return pedidos;
    }

    /**
     * Obtiene los detalles de un pedido específico
     * @param pedidoId ID del pedido
     * @return Lista de detalles del pedido
     */
    public static List<DetallePedido> obtenerDetallesPedido(int pedidoId) {
        List<DetallePedido> detalles = new ArrayList<>();
        String sql = "SELECT ip.*, p.precio FROM ItemPedido ip " +
                "JOIN Producto p ON ip.producto_id = p.id " +
                "WHERE ip.pedido_id = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productoId = rs.getInt("producto_id");
                    int cantidad = rs.getInt("cantidad");
                    float precioUnitario = rs.getFloat("precio");

                    DetallePedido detalle = new DetallePedido(productoId, cantidad, precioUnitario);
                    detalles.add(detalle);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalles del pedido: " + e.getMessage());
            e.printStackTrace();
        }

        return detalles;
    }
}

