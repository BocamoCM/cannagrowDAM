package com.example.cannagrow;

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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    // Configuración del logger
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            // Crear directorio de logs si no existe
            String userHome = System.getProperty("user.home");
            String appDir = userHome + File.separator + "AppData" + File.separator + "Local" + File.separator + "CannagrowApp" + File.separator + "logs";
            File logDir = new File(appDir);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Crear nombre de archivo con fecha y hora
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String logFile = appDir + File.separator + "cannagrow_" + timestamp + ".log";

            // Configurar el FileHandler para escribir en el archivo
            fileHandler = new FileHandler(logFile, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);

            // Establecer nivel de logging
            logger.setLevel(Level.ALL);

            logger.info("Aplicación Cannagrow iniciada");
        } catch (IOException e) {
            System.err.println("Error al configurar el sistema de logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            logger.info("Iniciando pantalla de login");
            Image image = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
            logoImage.setImage(image);
            logger.info("Logo cargado correctamente");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar el logo de la aplicación", e);
        }
    }

    @FXML
    protected void onLoginClick() {
        String email = usernameField.getText();
        String pass = passwordField.getText();

        logger.info("Intento de inicio de sesión para usuario: " + email);

        if (email.isEmpty() || pass.isEmpty()) {
            logger.warning("Intento de inicio de sesión con campos vacíos");
            loginMessage.setText("Por favor, completa todos los campos.");
            return;
        }

        try {
            boolean autenticado = usuarioModel.autenticarUsuario(email, pass);

            if (autenticado) {
                logger.info("Inicio de sesión exitoso para: " + email);
                loginMessage.setStyle("-fx-text-fill: green;");
                loginMessage.setText("Inicio de sesión exitoso.");

                try {
                    logger.info("Cargando pantalla de menú principal");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu.fxml"));
                    Parent menuRoot = loader.load();
                    Scene menuScene = new Scene(menuRoot);
                    Stage currentStage = (Stage) usernameField.getScene().getWindow();
                    currentStage.setScene(menuScene);
                    logger.info("Transición a pantalla de menú completada");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error al cargar la pantalla de menú", e);
                    loginMessage.setText("Error al cargar el menú.");
                }
            } else {
                logger.warning("Intento de inicio de sesión fallido para: " + email);
                loginMessage.setStyle("-fx-text-fill: red;");
                loginMessage.setText("Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado durante la autenticación", e);
            loginMessage.setStyle("-fx-text-fill: red;");
            loginMessage.setText("Error en el sistema de autenticación.");
        }
    }

    // Método para asegurar que el FileHandler se cierre correctamente
    public static void closeLogger() {
        if (fileHandler != null) {
            logger.info("Cerrando aplicación Cannagrow");
            fileHandler.close();
        }
    }
}