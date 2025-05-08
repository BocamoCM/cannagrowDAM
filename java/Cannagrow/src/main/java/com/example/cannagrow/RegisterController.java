package com.example.cannagrow;

import com.example.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> rolComboBox;

    @FXML
    private TextField salarioField;

    @FXML
    private Label registerMessage;

    private UsuarioModel usuarioModel;

    public RegisterController() {
        usuarioModel = new UsuarioModel();
    }

    @FXML
    public void onRegisterClick() {
        String nombre = nombreField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String rol = rolComboBox.getValue();
        String salarioStr = salarioField.getText();

        // Validaciones
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || rol == null || salarioStr.isEmpty()) {
            registerMessage.setText("Por favor, complete todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerMessage.setText("Las contraseñas no coinciden.");
            return;
        }

        double salario;
        try {
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException e) {
            registerMessage.setText("Salario no válido.");
            return;
        }

        // Llamada al modelo para registrar al usuario
        boolean success = usuarioModel.registrarUsuario(nombre, email, password, rol, salario);

        if (success) {
            registerMessage.setText("Usuario registrado exitosamente.");
        } else {
            registerMessage.setText("Error al registrar usuario.");
        }
    }
}
