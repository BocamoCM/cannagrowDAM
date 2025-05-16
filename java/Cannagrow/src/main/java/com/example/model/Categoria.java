package com.example.model;

/**
 * Representa una categoría dentro de la aplicación.
 *
 * Una categoría está compuesta por un nombre descriptivo y una URL de imagen asociada.
 * Esta clase puede utilizarse, por ejemplo, para clasificar productos, eventos u otras entidades
 * visuales que requieran una presentación con imagen.
 */
public class Categoria {
    private String nombre;
    private String imageUrl;

    /**
     * Crea una nueva categoría con el nombre e imagen proporcionados.
     *
     * @param nombre   El nombre de la categoría.
     * @param imageUrl La URL de la imagen asociada a la categoría.
     */
    public Categoria(String nombre, String imageUrl) {
        this.nombre = nombre;
        this.imageUrl = imageUrl;
    }

    /**
     * Obtiene el nombre de la categoría.
     *
     * @return El nombre de la categoría.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la URL de la imagen asociada a la categoría.
     *
     * @return La URL de la imagen.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Establece el nombre de la categoría.
     *
     * @param nombre El nuevo nombre de la categoría.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Establece la URL de la imagen asociada a la categoría.
     *
     * @param imageUrl La nueva URL de la imagen.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
