package com.example.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * Clase que representa un usuario en la aplicación.
 * Incluye métodos para autenticación, registro y actualización de datos del usuario.
 */
public class UsuarioModel {
    private String nombre;
    private String email;
    private String rol;
    private double salario;
    private int id;
    private String fotoPerfilUrl; // Campo para la foto de perfil
    private String discordId; // Campo para el ID de Discord (en Java usamos camelCase)

    /**
     * Autentica a un usuario buscando primero en la tabla Empleado y luego en Cliente.
     *
     * @param nombre     Nombre del usuario.
     * @param contrasena Contraseña ingresada.
     * @return El objeto UsuarioModel si la autenticación fue exitosa, null si falló.
     */
    public UsuarioModel autenticarUsuario(String nombre, String contrasena) {
        UsuarioModel usuario = autenticarDesdeTabla("Empleado", nombre, contrasena);

        if (usuario == null) {
            usuario = autenticarDesdeTabla("Cliente", nombre, contrasena);
        }

        return usuario;
    }

    /**
     * Autentica a un usuario dentro de una tabla específica (Empleado o Cliente).
     *
     * @param tabla      Nombre de la tabla a consultar.
     * @param nombre     Nombre del usuario.
     * @param contrasena Contraseña ingresada.
     * @return El objeto UsuarioModel si la autenticación fue exitosa, null si falló.
     */
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

    /**
     * Registra un nuevo usuario en la base de datos como Cliente o Empleado.
     *
     * @param nombre      Nombre del usuario.
     * @param email       Correo electrónico.
     * @param contrasena  Contraseña en texto plano (será hasheada).
     * @param rol         Rol del usuario (Cliente o Empleado).
     * @param salario     Salario del empleado (0 para Cliente).
     * @param discordId   ID de Discord (opcional).
     * @return true si el registro fue exitoso, false en caso contrario.
     */
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

    /**
     * Registra un nuevo cliente en la base de datos, incluyendo fecha de nacimiento.
     *
     * @param nombre           Nombre del cliente.
     * @param email            Correo electrónico.
     * @param contrasena       Contraseña en texto plano (será hasheada).
     * @param fechaNacimiento  Fecha de nacimiento del cliente.
     * @param discordId        ID de Discord (opcional).
     * @return true si el registro fue exitoso, false en caso contrario.
     */
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

    /**
     * Actualiza la URL de la foto de perfil del usuario en la base de datos.
     *
     * @param id              ID del usuario.
     * @param fotoPerfilUrl   Nueva URL de la foto de perfil.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
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

    /**
     * Actualiza el ID de Discord del usuario en la base de datos.
     *
     * @param id         ID del usuario.
     * @param discordId  Nuevo ID de Discord (puede ser null o vacío).
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
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

    /**
     * Actualiza la contraseña de un usuario en caso de que el hash actual sea inválido.
     *
     * @param nombre           Nombre del usuario.
     * @param nuevaContrasena  Nueva contraseña en texto plano.
     * @param tabla            Tabla en la que se encuentra el usuario (Empleado o Cliente).
     */
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
    /**
     * @return Nombre del usuario.
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del usuario.
     * @param nombre Nombre a asignar.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * @return Correo electrónico del usuario.
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del usuario.
     * @param email Correo electrónico a asignar.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @return Rol del usuario (Cliente o Empleado).
     */
    public String getRol() { return rol; }

    /**
     * Establece el rol del usuario.
     * @param rol Rol a asignar.
     */
    public void setRol(String rol) { this.rol = rol; }

    /**
     * @return Salario del usuario.
     */
    public double getSalario() { return salario; }

    /**
     * Establece el salario del usuario.
     * @param salario Salario a asignar.
     */
    public void setSalario(double salario) { this.salario = salario; }

    /**
     * @return ID único del usuario.
     */
    public int getId() { return id; }

    /**
     * Establece el ID del usuario.
     * @param id ID a asignar.
     */
    public void setId(int id) { this.id = id; }

    /**
     * @return URL de la foto de perfil del usuario.
     */
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }

    /**
     * Establece la URL de la foto de perfil del usuario.
     * @param fotoPerfilUrl URL de la foto a asignar.
     */
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }

    /**
     * @return ID de Discord del usuario.
     */
    public String getDiscordId() { return discordId; }

    /**
     * Establece el ID de Discord del usuario.
     * @param discordId ID de Discord a asignar.
     */
    public void setDiscordId(String discordId) { this.discordId = discordId; }

}