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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 720);

        // Añadir la hoja de estilos CSS a la escena
        String cssPath = "/styles.css";
        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

        stage.setTitle("Cannagrow");
        stage.getIcons().add(new Image(getClass().getResource("/com/example/cannagrow/images/cannagrow_logo.png").toExternalForm()));
        stage.setScene(scene);

        // Configurar el cierre de la aplicación
        configureCloseRequest(stage);

        stage.show();
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
                // Asegúrate de importar tu clase Session
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
        Session.cerrarSesion();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}