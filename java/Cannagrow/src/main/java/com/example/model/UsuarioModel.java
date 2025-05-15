package com.example.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UsuarioModel {
    private String nombre;
    private String email;
    private String rol;
    private double salario;
    private int id;
    private String fotoPerfilUrl; // Campo para la foto de perfil
    private String discordId; // Campo para el ID de Discord (en Java usamos camelCase)

    public UsuarioModel autenticarUsuario(String nombre, String contrasena) {
        UsuarioModel usuario = autenticarDesdeTabla("Empleado", nombre, contrasena);

        if (usuario == null) {
            usuario = autenticarDesdeTabla("Cliente", nombre, contrasena);
        }

        return usuario;
    }

    private UsuarioModel autenticarDesdeTabla(String tabla, String nombre, String contrasena) {
        String query = "SELECT id, contrasena_hash, email, fotoPerfilUrl, discordid as discordId" + // Corregido: discordid en la BD
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
                        usuario.setFotoPerfilUrl(rs.getString("fotoPerfilUrl"));
                        usuario.setDiscordId(rs.getString("discordId")); // Usando el alias que definimos en el SELECT

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

    public boolean registrarUsuario(String nombre, String email, String contrasena, String rol, double salario, String discordId) {
        String contrasenaHash = BCrypt.hashpw(contrasena, BCrypt.gensalt());
        String fotoPerfilPredeterminada = rol.equalsIgnoreCase("Cliente") ?
                "/com/example/cannagrow/img/perfil_cliente.png" :
                "/com/example/cannagrow/img/perfil_" + rol.toLowerCase() + ".png";

        String tabla = rol.equalsIgnoreCase("Cliente") ? "Cliente" : "Empleado";
        String query;

        if (tabla.equals("Cliente")) {
            query = "INSERT INTO Cliente (nombre, email, contrasena_hash, fotoPerfilUrl, discordid) VALUES (?, ?, ?, ?, ?)"; // Corregido: discordid
        } else {
            query = "INSERT INTO Empleado (nombre, email, contrasena_hash, rol, salario, fotoPerfilUrl, discordid) VALUES (?, ?, ?, ?, ?, ?, ?)"; // Corregido: discordid
        }

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, contrasenaHash);

            if (tabla.equals("Empleado")) {
                stmt.setString(4, rol);
                stmt.setDouble(5, salario);
                stmt.setString(6, fotoPerfilPredeterminada);

                // El ID de Discord es opcional
                if (discordId != null && !discordId.isEmpty()) {
                    stmt.setString(7, discordId);
                } else {
                    stmt.setNull(7, Types.VARCHAR);
                }
            } else {
                stmt.setString(4, fotoPerfilPredeterminada);

                // El ID de Discord es opcional
                if (discordId != null && !discordId.isEmpty()) {
                    stmt.setString(5, discordId);
                } else {
                    stmt.setNull(5, Types.VARCHAR);
                }
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarCliente(String nombre, String email, String contrasena, Date fechaNacimiento, String discordId) {
        String contrasenaHash = BCrypt.hashpw(contrasena, BCrypt.gensalt());
        String fotoPerfilPredeterminada = "/com/example/cannagrow/img/perfil_cliente.png";

        String query = "INSERT INTO Cliente (nombre, email, contrasena_hash, fechaNacimiento, fotoPerfilUrl, discordid) VALUES (?, ?, ?, ?, ?, ?)"; // Corregido: discordid

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, contrasenaHash);
            stmt.setDate(4, fechaNacimiento);
            stmt.setString(5, fotoPerfilPredeterminada);

            // El ID de Discord es opcional
            if (discordId != null && !discordId.isEmpty()) {
                stmt.setString(6, discordId);
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarFotoPerfil(int id, String fotoPerfilUrl) {
        String tabla = rol.equalsIgnoreCase("Cliente") ? "Cliente" : "Empleado";
        String query = "UPDATE " + tabla + " SET fotoPerfilUrl = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, fotoPerfilUrl);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarDiscordId(int id, String discordId) {
        String tabla = rol.equalsIgnoreCase("Cliente") ? "Cliente" : "Empleado";
        String query = "UPDATE " + tabla + " SET discordid = ? WHERE id = ?"; // Corregido: discordid

        try (Connection conn = DBUtil.getConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // El ID de Discord es opcional
            if (discordId != null && !discordId.isEmpty()) {
                stmt.setString(1, discordId);
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }

            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarContrasena(String nombre, String nuevaContrasena, String tabla) {
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

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }

    public String getDiscordId() { return discordId; }
    public void setDiscordId(String discordId) { this.discordId = discordId; }
}