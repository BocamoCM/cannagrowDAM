package com.example.model;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioModel {

    public boolean autenticarUsuario(String nombre, String contrasena) {
        String query = "SELECT contrasena_hash FROM Empleado WHERE nombre = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            // Comprobamos si existe un resultado con ese nombre
            if (rs.next()) {
                String hashGuardado = rs.getString("contrasena_hash");

                // Comprobamos si la contraseña proporcionada coincide con el hash
                boolean passwordCorrecta = BCrypt.checkpw(contrasena, hashGuardado);
                return passwordCorrecta;
            } else {
                // No existe ningún empleado con ese nombre
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarUsuario(String nombre, String email, String contrasena, String rol, double salario) {
        String query = "INSERT INTO Empleado (nombre, email, contrasena_hash, rol, salario) VALUES (?, ?, ?, ?, ?)";

        // Encriptamos la contraseña con BCrypt
        String contrasenaHash = BCrypt.hashpw(contrasena, BCrypt.gensalt());

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, contrasenaHash);
            stmt.setString(4, rol);
            stmt.setDouble(5, salario);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Si se afectaron filas, significa que se registró el usuario.

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
