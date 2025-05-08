package com.example.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoModel {

    public static List<Producto> obtenerTodos() {
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
            e.printStackTrace();
        }

        return productos;
    }
}
