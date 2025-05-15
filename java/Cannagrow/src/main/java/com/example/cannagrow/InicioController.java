package com.example.cannagrow;

import com.example.model.Categoria;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class InicioController {
    @FXML
    private FlowPane contenedorCategorias;

    @FXML
    public void initialize() {
        System.out.println("Iniciando inicialización de InicioController...");

        if (contenedorCategorias == null) {
            System.err.println("ERROR: contenedorCategorias es null en InicioController");
        } else {
            System.out.println("contenedorCategorias inicializado correctamente en InicioController");
            cargarCategorias();
        }
    }

    private void cargarCategorias() {
        System.out.println("Iniciando carga de categorías en InicioController...");

        // Limpiamos el contenedor primero para evitar duplicados
        contenedorCategorias.getChildren().clear();

        // Definimos categorías con sus imágenes
        Categoria[] categorias = {
                new Categoria("Fertilizantes", "/com/example/cannagrow/img/fertilizantes.png"),
                new Categoria("CBD", "/com/example/cannagrow/img/cbd.png"),
                new Categoria("Crecimiento", "/com/example/cannagrow/img/armarios.png"),
                new Categoria("Iluminacion","/com/example/cannagrow/img/iluminacion.png"),
                new Categoria("Macetas","/com/example/cannagrow/img/macetas.png"),
                new Categoria("Panes de seta","/com/example/cannagrow/img/pan.png"),
                new Categoria("Peladoras","/com/example/cannagrow/img/peladoras.png"),
                new Categoria("Semillas","/com/example/cannagrow/img/semillas.png"),




        };

        // Contador para verificar cuántas categorías se cargaron correctamente
        int categoriasExitosas = 0;

        for (Categoria cat : categorias) {
            try {
                System.out.println("Cargando categoría: " + cat.getNombre() + " con imagen: " + cat.getImageUrl());

                // Verificamos primero si la imagen existe
                InputStream testStream = getClass().getResourceAsStream(cat.getImageUrl());
                if (testStream == null) {
                    System.err.println("ADVERTENCIA: Imagen no encontrada en: " + cat.getImageUrl());

                    // Intentar con rutas alternativas
                    String nombreArchivo = cat.getImageUrl().substring(cat.getImageUrl().lastIndexOf('/') + 1);
                    String[] rutasAlternativas = {
                            "/img/" + nombreArchivo,
                            "/images/" + nombreArchivo,
                            "/" + nombreArchivo
                    };

                    for (String ruta : rutasAlternativas) {
                        System.out.println("Intentando con ruta alternativa: " + ruta);
                        if (getClass().getResourceAsStream(ruta) != null) {
                            cat.setImageUrl(ruta);
                            System.out.println("Imagen encontrada en ruta alternativa: " + ruta);
                            break;
                        }
                    }
                } else {
                    testStream.close(); // No olvidar cerrar el stream
                }

                // Cargar el componente FXML para la categoría
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/CategoriaItem.fxml"));
                Parent item = loader.load();

                CategoriaItemController controller = loader.getController();
                controller.setCategoria(cat);

                contenedorCategorias.getChildren().add(item);
                categoriasExitosas++;
                System.out.println("Categoría " + cat.getNombre() + " agregada al contenedor en InicioController");

            } catch (IOException e) {
                System.err.println("Error cargando categoría " + cat.getNombre() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }


        System.out.println("Total de categorías cargadas exitosamente en InicioController: " + categoriasExitosas + " de " + categorias.length);
    }

}