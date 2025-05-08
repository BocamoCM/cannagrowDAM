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

    @FXML
    private javafx.scene.control.DatePicker fechaNacimientoPicker;


    @FXML
    private ComboBox<String> tipoUsuarioComboBox;

    private UsuarioModel usuarioModel;

    public RegisterController() {
        usuarioModel = new UsuarioModel();
    }

    @FXML
    public void initialize() {
        tipoUsuarioComboBox.getItems().addAll("Empleado", "Cliente");
        rolComboBox.getItems().addAll("Admin", "Vendedor", "Gerente"); // Agrega aquí los roles disponibles

        // Ocultar salario y roles inicialmente
        salarioField.setVisible(false);
        salarioField.setManaged(false);
        rolComboBox.setDisable(true);
        rolComboBox.setManaged(true); // Mostrar espacio del ComboBox aunque esté desactivado

        tipoUsuarioComboBox.setOnAction(event -> {
            String tipo = tipoUsuarioComboBox.getValue();
            boolean esEmpleado = "Empleado".equalsIgnoreCase(tipo);

            salarioField.setVisible(esEmpleado);
            salarioField.setManaged(esEmpleado);

            rolComboBox.setDisable(!esEmpleado);
        });
    }

    @FXML
    public void onRegisterClick() {
        String tipoUsuario = tipoUsuarioComboBox.getValue();
        String nombre = nombreField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String rol = rolComboBox.getValue();
        String salarioStr = salarioField.getText();

        if (tipoUsuario == null || nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerMessage.setText("Por favor, complete todos los campos obligatorios.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerMessage.setText("Las contraseñas no coinciden.");
            return;
        }


        if ("Empleado".equalsIgnoreCase(tipoUsuario)) {
            if (rol == null || salarioStr.isEmpty()) {
                registerMessage.setText("Rol y salario son obligatorios para empleados.");
                return;
            }

            try {
                double salario = Double.parseDouble(salarioStr);
                boolean success = usuarioModel.registrarUsuario(nombre, email, password, rol, salario);
                registerMessage.setText(success ? "Empleado registrado." : "Error al registrar empleado.");
            } catch (NumberFormatException e) {
                registerMessage.setText("Salario no válido.");
            }

        } else if ("Cliente".equalsIgnoreCase(tipoUsuario)) {
            if (fechaNacimientoPicker.getValue() == null) {
                registerMessage.setText("La fecha de nacimiento es obligatoria para clientes.");
                return;
            }

            java.sql.Date fechaNacimiento = java.sql.Date.valueOf(fechaNacimientoPicker.getValue());
            boolean success = usuarioModel.registrarCliente(nombre, email, password, fechaNacimiento);
            registerMessage.setText(success ? "Cliente registrado." : "Error al registrar cliente.");
        }

    }
}
