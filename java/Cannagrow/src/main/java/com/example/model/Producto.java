package com.example.model;

/**
 * Representa un producto del sistema con sus características principales,
 * como contenido de THC y CBD, precio, stock e imagen asociada.
 */
public class Producto {
    private int id;
    private String nombre;
    private String tipo;
    private float contenidoTHC;
    private float contenidoCBD;
    private float precio;
    private int stock;
    private String imagenProducto;

    /**
     * Constructor con todos los parámetros del producto.
     *
     * @param id              Identificador único del producto
     * @param nombre          Nombre del producto
     * @param tipo            Tipo o categoría del producto
     * @param contenidoTHC    Porcentaje de THC en el producto
     * @param contenidoCBD    Porcentaje de CBD en el producto
     * @param precio          Precio del producto
     * @param stock           Cantidad disponible en inventario
     * @param imagenProducto  Ruta o nombre de archivo de la imagen del producto
     */
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

    /**
     * Constructor vacío para frameworks o inicialización posterior.
     */
    public Producto() {
    }

    /**
     * @return Identificador único del producto
     */
    public int getId() {
        return id;
    }

    /**
     * @param id Establece el identificador del producto
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre Establece el nombre del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return Tipo o categoría del producto
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo Establece el tipo o categoría del producto
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return Porcentaje de THC en el producto
     */
    public float getContenidoTHC() {
        return contenidoTHC;
    }

    /**
     * @param contenidoTHC Establece el contenido de THC
     */
    public void setContenidoTHC(float contenidoTHC) {
        this.contenidoTHC = contenidoTHC;
    }

    /**
     * @return Porcentaje de CBD en el producto
     */
    public float getContenidoCBD() {
        return contenidoCBD;
    }

    /**
     * @param contenidoCBD Establece el contenido de CBD
     */
    public void setContenidoCBD(float contenidoCBD) {
        this.contenidoCBD = contenidoCBD;
    }

    /**
     * @return Precio del producto
     */
    public float getPrecio() {
        return precio;
    }

    /**
     * @param precio Establece el precio del producto
     */
    public void setPrecio(float precio) {
        this.precio = precio;
    }

    /**
     * @return Stock disponible del producto
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock Establece la cantidad disponible en stock
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return Imagen asociada al producto
     */
    public String getImagenProducto() {
        return imagenProducto;
    }

    /**
     * @param imagenProducto Establece la imagen del producto
     */
    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
}
