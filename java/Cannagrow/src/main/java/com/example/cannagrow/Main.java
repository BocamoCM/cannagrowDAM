package com.example.cannagrow;

import com.example.model.Session;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            // Imprime la URL del recurso FXML para depuración
            var resourceUrl = Main.class.getResource("hello-view.fxml");
            System.out.println("URL del FXML: " + resourceUrl);

            // Carga el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
            Scene scene = new Scene(fxmlLoader.load(), 1000, 720);

            // Añadir la hoja de estilos CSS a la escena
            try {
                String cssPath = "/styles.css";
                var cssUrl = getClass().getResource(cssPath);
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                } else {
                    System.err.println("No se encontró el archivo CSS: " + cssPath);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar el CSS: " + e.getMessage());
            }

            stage.setTitle("Cannagroww");

            // Cargar el icono
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
                if (icon != null && !icon.isError()) {
                    stage.getIcons().add(icon);
                } else {
                    System.err.println("Error al cargar el icono: La imagen es nula o contiene errores");
                }
            } catch (Exception e) {
                System.err.println("Error al cargar el icono: " + e.getMessage());
                e.printStackTrace();
            }

            // Configurar escena y mostrar
            stage.setScene(scene);

            // Configurar el cierre de la aplicación
            configureCloseRequest(stage);

            stage.show();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();

            // Mostrar diálogo de error
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error de Inicialización");
            errorAlert.setHeaderText("Error al cargar la aplicación");
            errorAlert.setContentText("Detalles: " + e.getMessage());
            errorAlert.showAndWait();

            // Salir de la aplicación en caso de error crítico
            Platform.exit();
        }
    }

    private void configureCloseRequest(Stage stage) {
        stage.setOnCloseRequest(event -> {
            // Prevenir el cierre automático
            event.consume();

            // Mostrar diálogo de confirmación
            mostrarDialogoCerrar(stage);
        });
    }

    private void mostrarDialogoCerrar(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre");
        alert.setHeaderText("¿Está seguro que desea salir?");
        alert.setContentText("Se cerrará su sesión actual.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Cerrar sesión
                Session.cerrarSesion();

                // Salir de la aplicación
                Platform.exit();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        // Método de respaldo para asegurar cierre de sesión
        // En caso de que no se haya cerrado correctamente
        try {
            Session.cerrarSesion();
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}