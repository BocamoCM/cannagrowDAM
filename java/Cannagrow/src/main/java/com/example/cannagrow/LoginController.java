package com.example.cannagrow;

import com.example.model.Session;
import com.example.model.UsuarioModel;
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

    private final UsuarioModel usuarioModel = new UsuarioModel();

    @FXML
    public void initialize() {
        Image image = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
        logoImage.setImage(image);
    }

    @FXML
    protected void onLoginClick() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            loginMessage.setText("Por favor, completa todos los campos.");
            return;
        }

        // Usamos la nueva versión del método
        UsuarioModel usuarioAutenticado = usuarioModel.autenticarUsuario(user, pass);

        if (usuarioAutenticado != null) {
            loginMessage.setStyle("-fx-text-fill: green;");
            loginMessage.setText("Inicio de sesión exitoso.");

            // Guardamos el usuario autenticado en la sesión
            Session.setUsuarioActual(usuarioAutenticado);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu.fxml"));
                Parent menuRoot = loader.load();
                Scene menuScene = new Scene(menuRoot);
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.setScene(menuScene);
                currentStage.show();
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
