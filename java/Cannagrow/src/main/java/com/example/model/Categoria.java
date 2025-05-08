package com.example.model;

public class Categoria {
    private String nombre;
    private String imageUrl;

    public Categoria(String nombre, String imageUrl) {
        this.nombre = nombre;
        this.imageUrl = imageUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

