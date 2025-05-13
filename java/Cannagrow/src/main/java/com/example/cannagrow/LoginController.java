package com.example.cannagrow;

import com.example.model.Session;
import com.example.model.UsuarioModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
            logoImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLoginClick() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            loginMessage.setText("Por favor, completa todos los campos.");
            return;
        }

        UsuarioModel usuarioAutenticado = usuarioModel.autenticarUsuario(user, pass);

        if (usuarioAutenticado != null) {
            loginMessage.setStyle("-fx-text-fill: green;");
            loginMessage.setText("Inicio de sesión exitoso.");

            // Guarda el usuario en la sesión y registra el inicio en la base de datos
            Session.setUsuarioActual(usuarioAutenticado);

            try {
                // Cambiamos para cargar menu.fxml en lugar de inicio.fxml
                // De esta forma el MenuController se encargará de mostrar la vista de inicio
                // en su contenido central
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                SceneChanger.changeScene("/com/example/cannagrow/menu.fxml", currentStage);
            } catch (Exception e) {
                e.printStackTrace();
                loginMessage.setStyle("-fx-text-fill: red;");
                loginMessage.setText("Error al cargar el menú: " + e.getMessage());
            }

        } else {
            loginMessage.setStyle("-fx-text-fill: red;");
            loginMessage.setText("Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    public void onRegistrarseClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneChanger.changeScene("/com/example/cannagrow/registro.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            loginMessage.setStyle("-fx-text-fill: red;");
            loginMessage.setText("Error al cargar la página de registro: " + e.getMessage());
        }
    }
}