package com.example.cannagrow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneChanger {

    public static void changeScene(String fxml, Stage stage) {
        try {
            Parent root = FXMLLoader.load(SceneChanger.class.getResource(fxml));
            Scene scene = new Scene(root);

            // Añadir la hoja de estilos CSS a cada escena nueva
            String cssPath = "/styles.css";
            scene.getStylesheets().add(SceneChanger.class.getResource(cssPath).toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cambiar la escena a: " + fxml);
        }
    }
}