package com.example.cannagrow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}