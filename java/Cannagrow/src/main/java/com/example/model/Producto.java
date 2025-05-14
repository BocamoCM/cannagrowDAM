package com.example.model;

public class Producto {
    private int id;
    private String nombre;
    private String tipo;
    private float contenidoTHC;
    private float contenidoCBD;
    private float precio;
    private int stock;
    private String imagenProducto;

    // Constructor, getters y setters
    public Producto(int id, String nombre, String tipo, float contenidoTHC, float contenidoCBD, float precio, int stock, String imagenProducto) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.contenidoTHC = contenidoTHC;
        this.contenidoCBD = contenidoCBD;
        this.precio = precio;
        this.stock = stock;
        this.imagenProducto = imagenProducto;
    }

    public Producto() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getContenidoTHC() {
        return contenidoTHC;
    }

    public void setContenidoTHC(float contenidoTHC) {
        this.contenidoTHC = contenidoTHC;
    }

    public float getContenidoCBD() {
        return contenidoCBD;
    }

    public void setContenidoCBD(float contenidoCBD) {
        this.contenidoCBD = contenidoCBD;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
}
