package com.example.cannagrow;

import com.example.model.UsuarioModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    // Elementos comunes en ambos FXML
    @FXML private ImageView logoImage;
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private Label registerMessage;
    @FXML private TextField salarioField;

    // Elementos solo en el segundo FXML
    @FXML private ComboBox<String> tipoUsuarioComboBox;
    @FXML private ComboBox<String> rolComboBox;

    // Variable para identificar qué FXML está siendo usado
    private boolean isSecondForm = false;

    // Instancia del modelo de usuario
    private UsuarioModel usuarioModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Inicializar el modelo de usuario
            usuarioModel = new UsuarioModel();

            // Configuración del logo
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoImage.setImage(logo);

            // Detectar qué formulario estamos usando
            isSecondForm = isSecondFormLoaded();

            // Si estamos en el segundo formulario, configuramos los listeners
            if (isSecondForm) {
                configureSecondFormListeners();
            }

        } catch (Exception e) {
            System.err.println("Error al cargar recursos: " + e.getMessage());
        }
    }

    private boolean isSecondFormLoaded() {
        // Verificamos si existe el ComboBox tipoUsuarioComboBox
        // que solo existe en el segundo formulario
        return tipoUsuarioComboBox != null;
    }

    private void configureSecondFormListeners() {
        // Configurar listener para el ComboBox de tipo de usuario
        tipoUsuarioComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                boolean isEmpleado = "Empleado".equals(newVal);
                // Mostrar/ocultar campos específicos de empleado
                rolComboBox.setVisible(isEmpleado);
                rolComboBox.setManaged(isEmpleado);
                salarioField.setVisible(isEmpleado);
                salarioField.setManaged(isEmpleado);

                // Configurar opciones del rolComboBox según el tipo de usuario
                if (isEmpleado) {
                    rolComboBox.getItems().setAll("Vendedor", "Cultivador", "Gerente");
                    rolComboBox.getSelectionModel().selectFirst();
                } else {
                    rolComboBox.getItems().setAll("Cliente");
                    rolComboBox.getSelectionModel().selectFirst();
                }
            }
        });

        // Establecer selección inicial
        tipoUsuarioComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    protected void onRegisterClick(ActionEvent event) {
        if (validateFields()) {
            // Lógica para registrar usuario
            String nombre = nombreField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();

            // Datos específicos según formulario
            String tipo = isSecondForm ? tipoUsuarioComboBox.getValue() : "Cliente";
            String rol = isSecondForm ? rolComboBox.getValue() : "Cliente";
            Double salario = 0.0;

            if (isSecondForm && "Empleado".equals(tipo) && salarioField.isVisible()) {
                try {
                    salario = Double.parseDouble(salarioField.getText());
                } catch (NumberFormatException e) {
                    showError("El salario debe ser un número válido");
                    return;
                }
            }

            // Registrar el usuario utilizando UsuarioModel
            boolean registroExitoso = false;

            if ("Cliente".equals(tipo)) {
                // Convertir LocalDate a java.sql.Date para registrar cliente
                Date sqlFechaNacimiento = Date.valueOf(fechaNacimiento);
                registroExitoso = usuarioModel.registrarCliente(nombre, email, password, sqlFechaNacimiento);
            } else {
                // Registrar empleado
                registroExitoso = usuarioModel.registrarUsuario(nombre, email, password, rol, salario);
            }

            if (registroExitoso) {
                registerMessage.setTextFill(javafx.scene.paint.Color.GREEN);
                registerMessage.setText("Usuario registrado con éxito");

                // Redirigir a la pantalla de login después de 2 segundos
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::abrirLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                registerMessage.setTextFill(javafx.scene.paint.Color.RED);
                registerMessage.setText("Error al registrar usuario");
            }
        }
    }

    private boolean validateFields() {
        // Validar campos obligatorios
        if (nombreField.getText().isEmpty()) {
            showError("El nombre es obligatorio");
            return false;
        }

        if (emailField.getText().isEmpty() || !emailField.getText().contains("@")) {
            showError("Email inválido");
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showError("La contraseña es obligatoria");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Las contraseñas no coinciden");
            return false;
        }

        if (fechaNacimientoPicker.getValue() == null) {
            showError("La fecha de nacimiento es obligatoria");
            return false;
        }

        // Validar mayoría de edad (18 años)
        if (fechaNacimientoPicker.getValue().plusYears(18).isAfter(LocalDate.now())) {
            showError("Debes ser mayor de 18 años para registrarte");
            return false;
        }

        // Validar campos específicos según el formulario
        if (isSecondForm) {
            if (tipoUsuarioComboBox.getValue() == null) {
                showError("Selecciona un tipo de usuario");
                return false;
            }

            if (rolComboBox.isVisible() && rolComboBox.getValue() == null) {
                showError("Selecciona un rol");
                return false;
            }

            if (salarioField.isVisible() && salarioField.getText().isEmpty()) {
                showError("El salario es obligatorio para empleados");
                return false;
            }

            // Validar que el salario sea un número válido
            if (salarioField.isVisible()) {
                try {
                    Double.parseDouble(salarioField.getText());
                } catch (NumberFormatException e) {
                    showError("El salario debe ser un número válido");
                    return false;
                }
            }
        }

        return true;
    }

    private void showError(String message) {
        registerMessage.setTextFill(javafx.scene.paint.Color.RED);
        registerMessage.setText(message);
    }

    private void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nombreField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            registerMessage.setText("Error al cargar la pantalla de login");
        }
    }

    @FXML
    protected void onRegisterDosClick(ActionEvent event) {
        // Navegar hacia atrás o a otra pantalla
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nombreField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            registerMessage.setText("Error al cargar la pantalla anterior");
        }
    }
}