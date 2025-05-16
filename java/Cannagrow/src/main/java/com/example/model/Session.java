package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase de utilidad para gestionar la sesión actual del usuario en la aplicación.
 * Permite registrar, obtener y cerrar sesiones, y registra la información en la base de datos.
 */
public class Session {
    private static UsuarioModel usuarioActual;

    /**
     * Establece el usuario actualmente conectado y registra su sesión en la base de datos.
     *
     * @param usuario El usuario que inicia sesión
     */
    public static void setUsuarioActual(UsuarioModel usuario) {
        usuarioActual = usuario;
        if (usuario != null) {
            registrarSesion(usuario);
        }
    }

    /**
     * Obtiene el usuario que está actualmente conectado.
     *
     * @return El usuario actualmente en sesión, o null si no hay sesión activa
     */
    public static UsuarioModel getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión del usuario actual y actualiza la base de datos para reflejar el cierre.
     */
    public static void cerrarSesion() {
        if (usuarioActual != null) {
            cerrarSesionEnBD(usuarioActual);
            usuarioActual = null;
        }
    }

    /**
     * Registra en la base de datos que el usuario ha iniciado sesión.
     *
     * @param usuario El usuario cuya sesión se está iniciando
     */
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

    /**
     * Marca como finalizada en la base de datos la sesión activa del usuario.
     *
     * @param usuario El usuario cuya sesión se está cerrando
     */
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
