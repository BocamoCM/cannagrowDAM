package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Session {
    private static UsuarioModel usuarioActual;

    public static void setUsuarioActual(UsuarioModel usuario) {
        usuarioActual = usuario;
        if (usuario != null) {
            registrarSesion(usuario);
        }
    }

    public static UsuarioModel getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        if (usuarioActual != null) {
            cerrarSesionEnBD(usuarioActual);
            usuarioActual = null;
        }
    }

    private static void registrarSesion(UsuarioModel usuario) {
        String sql = "INSERT INTO SesionActiva (usuario_id, tipo_usuario, nombre_usuario) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getId());
            stmt.setString(2, usuario.getRol().equalsIgnoreCase("Cliente") ? "Cliente" : "Empleado");
            stmt.setString(3, usuario.getNombre());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cerrarSesionEnBD(UsuarioModel usuario) {
        String sql = "UPDATE SesionActiva SET fin_sesion = CURRENT_TIMESTAMP, activa = FALSE " +
                "WHERE usuario_id = ? AND activa = TRUE AND tipo_usuario = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getId());
            stmt.setString(2, usuario.getRol().equalsIgnoreCase("Cliente") ? "Cliente" : "Empleado");

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
