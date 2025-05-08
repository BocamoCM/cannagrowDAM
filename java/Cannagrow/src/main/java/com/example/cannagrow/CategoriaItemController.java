package com.example.cannagrow;

import com.example.model.Categoria;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class CategoriaItemController {
    @FXML
    private ImageView imageView;
    @FXML
    private Label nombreLabel;

    public void setCategoria(Categoria categoria) {
        System.out.println("setCategoria llamado con: " + categoria.getNombre());

        nombreLabel.setText(categoria.getNombre());

        // Intentar cargar la imagen primero desde la URL proporcionada
        cargarImagen(categoria.getImageUrl());
    }

    private void cargarImagen(String imagePath) {
        System.out.println("Intentando cargar imagen desde: " + imagePath);

        try {
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is != null) {
                imageView.setImage(new Image(is));
                System.out.println("Imagen cargada exitosamente desde: " + imagePath);
                return;
            } else {
                System.err.println("Recurso no encontrado: " + imagePath);
            }

            // Si llega aquí, intentar con rutas alternativas
            String nombreArchivo = imagePath.substring(imagePath.lastIndexOf('/') + 1);

            // Intentar varias rutas posibles
            String[] rutasAlternativas = {
                    "/img/" + nombreArchivo,
                    "/images/" + nombreArchivo,
                    "/" + nombreArchivo,
                    "/com/example/cannagrow/images/" + nombreArchivo,
                    "/com/example/cannagrow/" + nombreArchivo
            };

            for (String ruta : rutasAlternativas) {
                System.out.println("Intentando ruta alternativa: " + ruta);
                InputStream altStream = getClass().getResourceAsStream(ruta);
                if (altStream != null) {
                    imageView.setImage(new Image(altStream));
                    System.out.println("Imagen cargada exitosamente desde ruta alternativa: " + ruta);
                    return;
                }
            }

            // Si llegamos aquí, intentemos cargar una imagen genérica
            cargarImagenGenerica();

        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            e.printStackTrace();
            cargarImagenGenerica();
        }
    }

    private void cargarImagenGenerica() {
        try {
            // Intentar cargar una imagen de placeholder
            String[] placeholders = {
                    "/com/example/cannagrow/placeholder.png",
                    "/placeholder.png",
                    "/com/example/cannagrow/default.png",
                    "/default.png"
            };

            for (String placeholder : placeholders) {
                InputStream placeholderStream = getClass().getResourceAsStream(placeholder);
                if (placeholderStream != null) {
                    imageView.setImage(new Image(placeholderStream));
                    System.out.println("Cargada imagen genérica desde: " + placeholder);
                    return;
                }
            }

            // Si no podemos cargar ni siquiera un placeholder, crear un texto que lo indique
            System.err.println("No se pudo cargar ninguna imagen de placeholder");
        } catch (Exception ex) {
            System.err.println("Error cargando imagen genérica: " + ex.getMessage());
        }
    }
}