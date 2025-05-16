package com.example.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoModel {

    /**
     * Obtiene todos los productos de la base de datos
     * @return Lista de productos
     */
    public static List<Producto> obtenerTodos() {  // Fixed method name from "obtenearTodos" to "obtenerTodos"
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto";

        try (Connection conn = DBUtil.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getFloat("contenidoTHC"),
                        rs.getFloat("contenidoCBD"),
                        rs.getFloat("precio"),
                        rs.getInt("stock"),
                        rs.getString("imagen_producto")
                );
                productos.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Obtiene productos filtrados por tipo
     * @param tipo El tipo de producto a filtrar
     * @return Lista de productos del tipo especificado
     */
    public static List<Producto> obtenerPorTipo(String tipo) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE tipo = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("tipo"),
                            rs.getFloat("contenidoTHC"),
                            rs.getFloat("contenidoCBD"),
                            rs.getFloat("precio"),
                            rs.getInt("stock"),
                            rs.getString("imagen_producto")
                    );
                    productos.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al filtrar productos por tipo: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Obtiene todos los tipos de productos disponibles
     * @return Lista de tipos de productos
     */
    public static List<String> obtenerTiposDisponibles() {
        List<String> tipos = new ArrayList<>();
        String sql = "SELECT DISTINCT tipo FROM Producto ORDER BY tipo";

        try (Connection conn = DBUtil.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tipos.add(rs.getString("tipo"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de productos: " + e.getMessage());
            e.printStackTrace();

            // Si hay error, devolvemos los tipos fijos como fallback
            tipos.add("Flor");
            tipos.add("Aceite");
            tipos.add("Comestible");
            tipos.add("Extracto");
            tipos.add("Semilla");
            tipos.add("Artilujos");
            tipos.add("Paquete");
            tipos.add("Ropa");
            tipos.add("Recurso");
            tipos.add("Bebida");
            tipos.add("Cosmético");
            tipos.add("Mascotas");
        }

        return tipos;
    }

    /**
     * Busca productos por nombre
     * @param termino Término de búsqueda
     * @return Lista de productos que coinciden con la búsqueda
     */
    public static List<Producto> buscarPorNombre(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE nombre LIKE ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + termino + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("tipo"),
                            rs.getFloat("contenidoTHC"),
                            rs.getFloat("contenidoCBD"),
                            rs.getFloat("precio"),
                            rs.getInt("stock"),
                            rs.getString("imagen_producto")
                    );
                    productos.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Busca productos por nombre
     * @param producto Término de búsqueda
     * @return Lista de productos que coinciden con la búsqueda
     */
    public static boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE Producto SET nombre=?, tipo=?, contenidoTHC=?, contenidoCBD=?, precio=?, stock=? WHERE id=?";
        try (Connection conn = DBUtil.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getTipo());
            pstmt.setFloat(3, producto.getContenidoTHC());
            pstmt.setFloat(4, producto.getContenidoCBD());
            pstmt.setFloat(5, producto.getPrecio());
            pstmt.setInt(6, producto.getStock());
            pstmt.setInt(7, producto.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // For ProductoModel class
    /**
     * Obtains a product by its ID
     * @param id The product ID to search for
     * @return The Producto object if found, null otherwise
     */
    public static Producto obtenerPorId(int id) {
        Producto producto = null;
        String sql = "SELECT * FROM Producto WHERE id = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto();
                    producto.setId(rs.getInt("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setTipo(rs.getString("tipo"));
                    producto.setContenidoTHC(rs.getFloat("contenidoTHC"));
                    producto.setContenidoCBD(rs.getFloat("contenidoCBD"));
                    producto.setPrecio(rs.getFloat("precio"));
                    producto.setStock(rs.getInt("stock"));


                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return producto;
    }
}