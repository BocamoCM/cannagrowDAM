package com.example.model;

public class Session {
    private static UsuarioModel usuarioActual;

    public static void setUsuarioActual(UsuarioModel usuario) {
        usuarioActual = usuario;
    }

    public static UsuarioModel getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
