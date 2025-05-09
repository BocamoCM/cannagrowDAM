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
    private int id;// Solo se usará para empleados

    public UsuarioModel autenticarUsuario(String nombre, String contrasena) {
        UsuarioModel usuario = autenticarDesdeTabla("Empleado", nombre, contrasena);

        if (usuario == null) {
            usuario = autenticarDesdeTabla("Cliente", nombre, contrasena);
        }

        return usuario;
    }

    private UsuarioModel autenticarDesdeTabla(String tabla, String nombre, String contrasena) {
        String query = "SELECT id, contrasena_hash, email" +
                (tabla.equals("Empleado") ? ", rol, salario" : "") +
                " FROM " + tabla + " WHERE nombre = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashGuardado = rs.getString("contrasena_hash");

                try {
                    if (BCrypt.checkpw(contrasena, hashGuardado)) {
                        UsuarioModel usuario = new UsuarioModel();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNombre(nombre);
                        usuario.setEmail(rs.getString("email"));

                        if (tabla.equals("Empleado")) {
                            usuario.setRol(rs.getString("rol"));
                            usuario.setSalario(rs.getDouble("salario"));
                        } else {
                            usuario.setRol("Cliente");
                            usuario.setSalario(0); // opcional
                        }

                        return usuario;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Hash inválido en " + tabla + ". Actualizando...");
                    actualizarContrasena(nombre, contrasena, tabla);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean registrarUsuario(String nombre, String email, String contrasena, String rol, double salario) {
        String contrasenaHash = BCrypt.hashpw(contrasena, BCrypt.gensalt());

        String tabla = rol.equalsIgnoreCase("Cliente") ? "Cliente" : "Empleado";
        String query;

        if (tabla.equals("Cliente")) {
            query = "INSERT INTO Cliente (nombre, email, contrasena_hash) VALUES (?, ?, ?)";
        } else {
            query = "INSERT INTO Empleado (nombre, email, contrasena_hash, rol, salario) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, contrasenaHash);

            if (tabla.equals("Empleado")) {
                stmt.setString(4, rol);
                stmt.setDouble(5, salario);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void actualizarContrasena(String nombre, String nuevaContrasena, String tabla) {
        String nuevaHash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
        String query = "UPDATE " + tabla + " SET contrasena_hash = ? WHERE nombre = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nuevaHash);
            stmt.setString(2, nombre);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Contraseña actualizada en " + tabla);
            } else {
                System.out.println("Usuario no encontrado en " + tabla);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registrarCliente(String nombre, String email, String contrasena, java.sql.Date fechaNacimiento) {
        String contrasenaHash = BCrypt.hashpw(contrasena, BCrypt.gensalt());

        String query = "INSERT INTO Cliente (nombre, email, contrasena_hash, fechaNacimiento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, contrasenaHash);
            stmt.setDate(4, fechaNacimiento);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
