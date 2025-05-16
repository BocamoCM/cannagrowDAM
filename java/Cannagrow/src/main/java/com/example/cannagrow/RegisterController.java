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
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controlador para el registro de usuarios
 */
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
    @FXML private TextField discordIdField; // Nuevo campo para ID de Discord

    // Elementos solo en el segundo FXML
    @FXML private ComboBox<String> tipoUsuarioComboBox;
    @FXML private ComboBox<String> rolComboBox;

    // Variable para identificar qué FXML está siendo usado
    private boolean isSecondForm = false;

    // Instancia del modelo de usuario
    private UsuarioModel usuarioModel;

    /**
     * Inicializa el controlador después de que su elemento raíz haya sido completamente procesado.
     * Configura el logo, detecta el tipo de formulario actual y prepara los componentes necesarios.
     *
     * @param url URL de ubicación utilizada para resolver rutas relativas
     * @param resourceBundle Recursos específicos de localización
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Inicializar el modelo de usuario
            usuarioModel = new UsuarioModel();

            // Configuración del logo
            try {
                // Corregido: Ruta ajustada para cargar el recurso correctamente
                Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png")));
                logoImage.setImage(logo);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen del logo: " + e.getMessage());
                // Continuar sin la imagen para no bloquear la inicialización
            }

            // Detectar qué formulario estamos usando basado en la existencia de componentes
            detectFormType();

            // Si estamos en el segundo formulario, configuramos los componentes adicionales
            if (isSecondForm) {
                configureSecondFormComponents();
            }

        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Detecta si el formulario actual es el formulario secundario, basado en la presencia
     * de ciertos componentes de interfaz de usuario.
     */
    private void detectFormType() {
        // Verificamos si los componentes del segundo formulario están presentes
        try {
            isSecondForm = tipoUsuarioComboBox != null && rolComboBox != null;
        } catch (Exception e) {
            isSecondForm = false;
        }
    }

    /**
     * Configura los componentes específicos del segundo formulario, como los ComboBox de tipo
     * de usuario y roles, incluyendo sus opciones iniciales y comportamiento.
     */
    private void configureSecondFormComponents() {
        try {
            // Configurar las opciones iniciales del ComboBox de tipo de usuario
            if (tipoUsuarioComboBox != null) {
                tipoUsuarioComboBox.getItems().setAll("Cliente", "Empleado");

                // Configurar listener para el ComboBox de tipo de usuario
                tipoUsuarioComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        updateUIBasedOnUserType(newVal);
                    }
                });

                // Selección inicial
                tipoUsuarioComboBox.getSelectionModel().select("Cliente");
            }

            // Inicializar rolComboBox
            if (rolComboBox != null) {
                rolComboBox.getItems().setAll("Cliente");
                rolComboBox.getSelectionModel().select("Cliente");

                // Inicialmente ocultar campos específicos de empleado
                updateUIBasedOnUserType("Cliente");
            }
        } catch (Exception e) {
            System.err.println("Error configurando componentes del segundo formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la interfaz de usuario según el tipo de usuario seleccionado ("Cliente" o "Empleado"),
     * mostrando u ocultando componentes relevantes como el rol y salario.
     *
     * @param userType Tipo de usuario seleccionado
     */
    private void updateUIBasedOnUserType(String userType) {
        try {
            boolean isEmpleado = "Empleado".equals(userType);

            // Asegurarse de que los componentes existen antes de intentar modificarlos
            if (rolComboBox != null) {
                rolComboBox.setVisible(isEmpleado);
                rolComboBox.setManaged(isEmpleado);

                // Actualizar opciones según el tipo
                if (isEmpleado) {
                    rolComboBox.getItems().setAll("Vendedor", "Cultivador", "Gerente");
                    rolComboBox.getSelectionModel().selectFirst();
                } else {
                    rolComboBox.getItems().setAll("Cliente");
                    rolComboBox.getSelectionModel().select("Cliente");
                }
            }

            if (salarioField != null) {
                salarioField.setVisible(isEmpleado);
                salarioField.setManaged(isEmpleado);
                if (!isEmpleado) {
                    salarioField.clear();
                }
            }
        } catch (Exception e) {
            System.err.println("Error actualizando UI basado en tipo de usuario: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de clic en el botón de registro. Valida los campos,
     * recoge los datos del formulario y llama al modelo para registrar al usuario.
     *
     * @param event Evento de acción asociado al clic del botón
     */
    @FXML
    protected void onRegisterClick(ActionEvent event) {
        try {
            if (validateFields()) {
                // Obtener valores de los campos
                String nombre = nombreField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText();
                LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();

                // Obtener el ID de Discord (opcional)
                String discordId = discordIdField != null ? discordIdField.getText().trim() : "";

                // Si está vacío, pasar null para que sea tratado como NULL en la base de datos
                if (discordId.isEmpty()) {
                    discordId = null;
                }

                // Datos específicos según formulario
                String tipo = isSecondForm && tipoUsuarioComboBox != null ?
                        tipoUsuarioComboBox.getValue() : "Cliente";
                String rol = isSecondForm && rolComboBox != null && rolComboBox.isVisible() ?
                        rolComboBox.getValue() : "Cliente";
                Double salario = 0.0;

                if (isSecondForm && "Empleado".equals(tipo) && salarioField != null && salarioField.isVisible()) {
                    try {
                        salario = Double.parseDouble(salarioField.getText().trim());
                        if (salario < 0) {
                            showMessage("El salario no puede ser negativo", true);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showMessage("El salario debe ser un número válido", true);
                        return;
                    }
                }

                // Registrar el usuario utilizando UsuarioModel
                boolean registroExitoso = false;

                try {
                    if ("Cliente".equals(tipo)) {
                        // Convertir LocalDate a java.sql.Date para registrar cliente
                        Date sqlFechaNacimiento = Date.valueOf(fechaNacimiento);
                        registroExitoso = usuarioModel.registrarCliente(nombre, email, password, sqlFechaNacimiento, discordId);
                    } else {
                        // Registrar empleado
                        registroExitoso = usuarioModel.registrarUsuario(nombre, email, password, rol, salario, discordId);
                    }
                } catch (Exception e) {
                    System.err.println("Error al registrar usuario: " + e.getMessage());
                    e.printStackTrace();
                    showMessage("Error en el sistema: " + e.getMessage(), true);
                    return;
                }

                if (registroExitoso) {
                    showMessage("Usuario registrado con éxito", false);

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
                    showMessage("Error al registrar usuario. El correo electrónico podría estar en uso.", true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error inesperado en registro: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error inesperado: " + e.getMessage(), true);
        }
    }

    /**
     * Valida los campos del formulario según reglas específicas como formato de email,
     * coincidencia de contraseñas, mayoría de edad, entre otras.
     *
     * @return true si todos los campos son válidos; false en caso contrario
     */
    private boolean validateFields() {
        // Validar campos obligatorios
        if (nombreField.getText().trim().isEmpty()) {
            showMessage("El nombre es obligatorio", true);
            return false;
        }

        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            showMessage("Email inválido. Debe contener @ y dominio válido", true);
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showMessage("La contraseña es obligatoria", true);
            return false;
        }

        if (passwordField.getText().length() < 6) {
            showMessage("La contraseña debe tener al menos 6 caracteres", true);
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showMessage("Las contraseñas no coinciden", true);
            return false;
        }

        if (fechaNacimientoPicker.getValue() == null) {
            showMessage("La fecha de nacimiento es obligatoria", true);
            return false;
        }

        // Validar mayoría de edad (18 años)
        if (fechaNacimientoPicker.getValue().plusYears(18).isAfter(LocalDate.now())) {
            showMessage("Debes ser mayor de 18 años para registrarte", true);
            return false;
        }

        // Validación opcional para el ID de Discord
        if (discordIdField != null && !discordIdField.getText().trim().isEmpty()) {
            String discordId = discordIdField.getText().trim();
            // Formato básico para Discord ID: nombre#0000 o simplemente el número de ID
            if (!discordId.matches("^[\\w\\s]+#\\d{4}$") && !discordId.matches("^\\d{17,20}$")) {
                showMessage("Formato de Discord ID inválido. Use nombre#0000 o ID numérico", true);
                return false;
            }
        }

        // Validar campos específicos según el formulario
        if (isSecondForm) {
            if (tipoUsuarioComboBox != null && tipoUsuarioComboBox.getValue() == null) {
                showMessage("Selecciona un tipo de usuario", true);
                return false;
            }

            if (rolComboBox != null && rolComboBox.isVisible() && rolComboBox.getValue() == null) {
                showMessage("Selecciona un rol", true);
                return false;
            }

            if (salarioField != null && salarioField.isVisible() && salarioField.getText().trim().isEmpty()) {
                showMessage("El salario es obligatorio para empleados", true);
                return false;
            }

            // Validar que el salario sea un número válido
            if (salarioField != null && salarioField.isVisible()) {
                try {
                    double salario = Double.parseDouble(salarioField.getText().trim());
                    if (salario < 0) {
                        showMessage("El salario no puede ser negativo", true);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    showMessage("El salario debe ser un número válido", true);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Muestra un mensaje de estado (éxito o error) al usuario en la interfaz.
     *
     * @param message Mensaje a mostrar
     * @param isError Indica si el mensaje es de error (true) o de éxito (false)
     */
    private void showMessage(String message, boolean isError) {
        if (registerMessage != null) {
            registerMessage.setTextFill(isError ?
                    javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
            registerMessage.setText(message);
        }
    }

    /**
     * Abre la pantalla de login reemplazando la vista actual.
     * Se utiliza tras un registro exitoso o al navegar de regreso.
     */
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
            showMessage("Error al cargar la pantalla de login", true);
        }
    }

    /**
     * Maneja el evento de navegación cuando el usuario quiere regresar
     * a la pantalla anterior desde el formulario de registro.
     *
     * @param event Evento de acción generado por la interacción del usuario
     */
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
            showMessage("Error al cargar la pantalla anterior", true);
        }
    }
}