package com.example.cannagrow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Utilidad para cambiar entre escenas de la aplicación.
 * Maneja la carga de archivos FXML y hojas de estilo.
 */
public class SceneChanger {

    private static final String CSS_PATH = "/styles.css";

    /**
     * Cambia la escena actual por una nueva basada en el archivo FXML proporcionado.
     *
     * @param fxmlPath Ruta al archivo FXML
     * @param stage Stage donde se cargará la nueva escena
     * @return true si el cambio de escena fue exitoso, false en caso contrario
     */
    public static boolean changeScene(String fxmlPath, Stage stage) {
        try {
            // Validamos los parámetros
            Objects.requireNonNull(fxmlPath, "La ruta FXML no puede ser nula");
            Objects.requireNonNull(stage, "El stage no puede ser nulo");

            // Obtenemos la URL del recurso
            URL fxmlUrl = SceneChanger.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("No se pudo encontrar el archivo FXML: " + fxmlPath);
            }

            // Cargamos el FXML
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            // Añadimos la hoja de estilos CSS si existe
            URL cssUrl = SceneChanger.class.getResource(CSS_PATH);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("Advertencia: No se encontró la hoja de estilos en: " + CSS_PATH);
            }

            // Establecemos la nueva escena y la mostramos
            stage.setScene(scene);
            stage.show();

            return true;

        } catch (IOException e) {
            handleError("Error al cargar la vista",
                    "No se pudo cargar la escena: " + fxmlPath, e);
            return false;
        } catch (Exception e) {
            handleError("Error inesperado",
                    "Ocurrió un error al cambiar a la escena: " + fxmlPath, e);
            return false;
        }
    }

    /**
     * Sobrecarga que permite especificar un título para la ventana.
     *
     * @param fxmlPath Ruta al archivo FXML
     * @param stage Stage donde se cargará la nueva escena
     * @param title Título para la ventana
     * @return true si el cambio de escena fue exitoso, false en caso contrario
     */
    public static boolean changeScene(String fxmlPath, Stage stage, String title) {
        boolean result = changeScene(fxmlPath, stage);
        if (result && title != null) {
            stage.setTitle(title);
        }
        return result;
    }

    /**
     * Maneja los errores mostrando un diálogo de alerta y registrando el error.
     *
     * @param title Título del diálogo de error
     * @param message Mensaje de error
     * @param exception Excepción que causó el error
     */
    private static void handleError(String title, String message, Exception exception) {
        // Registramos el error en la consola
        System.err.println(message);
        exception.printStackTrace();

        // Mostramos un diálogo de alerta
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + exception.getMessage());
        alert.showAndWait();
    }
}