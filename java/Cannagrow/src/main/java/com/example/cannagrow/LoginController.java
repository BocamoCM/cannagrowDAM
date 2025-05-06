package com.example.cannagrow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessage;

    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        Image image = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
        logoImage.setImage(image);
    }


    // Puedes cambiar esto por una verificación en base de datos
    private final String validUsername = "admin";
    private final String validPassword = "12345";

    @FXML
    protected void onLoginClick() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            loginMessage.setText("Por favor, completa todos los campos.");
            return;
        }

        if (user.equals(validUsername) && pass.equals(validPassword)) {
            loginMessage.setStyle("-fx-text-fill: green;");
            loginMessage.setText("Inicio de sesión exitoso.");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu.fxml"));
                Parent menuRoot = loader.load();
                Scene menuScene = new Scene(menuRoot);
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.setScene(menuScene);
            } catch (IOException e) {
                e.printStackTrace();
                loginMessage.setText("Error al cargar el menú.");
            }
        } else {
            loginMessage.setStyle("-fx-text-fill: red;");
            loginMessage.setText("Usuario o contraseña incorrectos.");
        }
    }

}
