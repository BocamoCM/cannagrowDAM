package com.example.model;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioModel {

    public boolean autenticarUsuario(String nombre, String contrasena) {
        String query = "SELECT contrasena_hash, email, rol, salario FROM Empleado WHERE nombre = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            // Comprobamos si existe un resultado con ese nombre
            if (rs.next()) {
                String hashGuardado = rs.getString("contrasena_hash");

                try {
                    // Verificamos la contraseña proporcionada
                    boolean passwordCorrecta = BCrypt.checkpw(contrasena, hashGuardado);
                    if (passwordCorrecta) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    // Si el hash es incompatible, actualizamos la contraseña
                    System.out.println("Error con el formato del hash de la contraseña. Actualizando...");

                    // Actualizamos solo el hash de la contraseña sin intentar registrar al usuario de nuevo
                    actualizarContrasena(nombre, contrasena); // Método que actualiza solo el hash de la contraseña
                    return BCrypt.checkpw(contrasena, BCrypt.hashpw(contrasena, BCrypt.gensalt()));
                }
            } else {
                // No existe ningún empleado con ese nombre
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Método para registrar un nuevo usuario
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

    // Método para actualizar la contraseña de un usuario (en caso de ser necesario)
    public void actualizarContrasena(String nombre, String nuevaContrasena) {
        String query = "UPDATE Empleado SET contrasena_hash = ? WHERE nombre = ?";

        // Encriptamos la nueva contraseña con BCrypt
        String nuevaContrasenaHash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nuevaContrasenaHash);
            stmt.setString(2, nombre);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contraseña actualizada correctamente.");
            } else {
                System.out.println("No se encontró un usuario con ese nombre.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
