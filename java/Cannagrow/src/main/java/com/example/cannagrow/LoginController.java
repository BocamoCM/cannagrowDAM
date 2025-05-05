package com.example.cannagrow;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessage;

    // Puedes cambiar esto por una verificación en base de datos
    private final String validUsername = "admin";
    private final String validPassword = "1234";

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
            // Aquí puedes abrir la siguiente ventana o cargar otra escena
        } else {
            loginMessage.setStyle("-fx-text-fill: red;");
            loginMessage.setText("Usuario o contraseña incorrectos.");
        }
    }
}
