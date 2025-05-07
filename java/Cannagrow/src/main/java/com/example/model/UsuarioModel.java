package com.example.model;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioModel {
    private String nombre;
    private String email;
    private String rol;
    private double salario;

    public UsuarioModel autenticarUsuario(String nombre, String contrasena) {
        String query = "SELECT contrasena_hash, email, rol, salario FROM Empleado WHERE nombre = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashGuardado = rs.getString("contrasena_hash");

                try {
                    boolean passwordCorrecta = BCrypt.checkpw(contrasena, hashGuardado);
                    if (passwordCorrecta) {
                        UsuarioModel usuario = new UsuarioModel();
                        usuario.setNombre(nombre);
                        usuario.setEmail(rs.getString("email"));
                        usuario.setRol(rs.getString("rol"));
                        usuario.setSalario(rs.getDouble("salario"));
                        return usuario;
                    } else {
                        return null;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error con el formato del hash de la contraseña. Actualizando...");
                    actualizarContrasena(nombre, contrasena);
                    // Puedes decidir si aquí también devuelves el usuario o no. Por ahora devuelvo null:
                    return null;
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
}
