package com.example.cannagrow;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase utilitaria para gestionar el caché de imágenes.
 * Almacena las imágenes ya cargadas para evitar cargarlas repetidamente.
 */
public class ImageCache {
    // Mapa que sirve como caché. La clave es la URL o ruta de la imagen, el valor es la imagen cargada
    private static final Map<String, Image> imageCache = new HashMap<>();

    /**
     * Obtiene una imagen desde el caché si existe, o la carga y la almacena si no.
     *
     * @param url La URL o ruta de la imagen
     * @return La imagen cargada, o null si ocurre un error
     */
    public static Image getImage(String url) {
        // Si la URL es nula o vacía, devolver null
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Comprobar si la imagen ya está en caché
        if (imageCache.containsKey(url)) {
            return imageCache.get(url);
        }

        try {
            Image image;
            // Cargar imagen desde recursos o URL externa
            if (url.startsWith("/")) {
                InputStream is = ImageCache.class.getResourceAsStream(url);
                if (is != null) {
                    image = new Image(is);
                    imageCache.put(url, image);
                    return image;
                }

                // Intentar rutas alternativas
                String[] alternativas = {
                        "/com/example/cannagrow" + url,
                        "/img" + url.substring(url.lastIndexOf('/')),
                        "/images" + url.substring(url.lastIndexOf('/'))
                };

                for (String alt : alternativas) {
                    InputStream altIs = ImageCache.class.getResourceAsStream(alt);
                    if (altIs != null) {
                        image = new Image(altIs);
                        // Guardar en caché con la URL original para futuras referencias
                        imageCache.put(url, image);
                        return image;
                    }
                }
            } else {
                // Para URLs externas o rutas de sistema de archivos
                image = new Image(url, true);
                imageCache.put(url, image);
                return image;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }

        return null;
    }

    /**
     * Obtiene la imagen por defecto cuando no se puede cargar una imagen específica.
     *
     * @return La imagen por defecto, o null si no se puede cargar
     */
    public static Image getDefaultImage() {
        // Clave para la imagen por defecto en el caché
        String defaultKey = "__DEFAULT__";

        // Comprobar si la imagen por defecto ya está en caché
        if (imageCache.containsKey(defaultKey)) {
            return imageCache.get(defaultKey);
        }

        try {
            // Intentar cargar la imagen por defecto
            InputStream is = ImageCache.class.getResourceAsStream("/com/example/cannagrow/img/sin_imagen.png");
            if (is != null) {
                Image defaultImage = new Image(is);
                imageCache.put(defaultKey, defaultImage);
                return defaultImage;
            }

            // Intentar rutas alternativas
            String[] alternativas = {
                    "/img/sin_imagen.png",
                    "/images/sin_imagen.png",
                    "/sin_imagen.png"
            };

            for (String alt : alternativas) {
                InputStream altIs = ImageCache.class.getResourceAsStream(alt);
                if (altIs != null) {
                    Image defaultImage = new Image(altIs);
                    imageCache.put(defaultKey, defaultImage);
                    return defaultImage;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen por defecto: " + e.getMessage());
        }

        return null;
    }

    /**
     * Limpia el caché de imágenes para liberar memoria.
     * Útil cuando se cierra la aplicación o se cambia a otra sección.
     */
    public static void clearCache() {
        imageCache.clear();
    }
}