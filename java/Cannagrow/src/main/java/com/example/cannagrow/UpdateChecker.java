package com.example.cannagrow;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Sistema de comprobación de actualizaciones para Cannagrow
 */
public class UpdateChecker {

    // La versión actual de la aplicación - Coincide con el valor en jpackage
    private static final String CURRENT_VERSION = "1.0.0";

    // URL donde se almacena el archivo de versión
    private static final String VERSION_URL = "https://raw.githubusercontent.com/yourusername/cannagrowDAM/main/version.txt";

    // URL donde el usuario puede descargar la nueva versión
    private static final String DOWNLOAD_URL = "https://github.com/yourusername/cannagrowDAM/releases/latest";

    // Período de comprobación en horas
    private static final int CHECK_PERIOD_HOURS = 24;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Stage primaryStage;

    public UpdateChecker(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Inicia el comprobador de actualizaciones
     */
    public void start() {
        // Comprobar actualizaciones al iniciar
        checkForUpdates();

        // Programar comprobaciones periódicas
        scheduler.scheduleAtFixedRate(
                this::checkForUpdates,
                CHECK_PERIOD_HOURS,
                CHECK_PERIOD_HOURS,
                TimeUnit.HOURS
        );
    }

    /**
     * Detiene el comprobador de actualizaciones
     */
    public void stop() {
        scheduler.shutdown();
    }

    /**
     * Comprueba si hay actualizaciones disponibles
     */
    private void checkForUpdates() {
        try {
            String latestVersion = getLatestVersion();
            if (isNewerVersion(latestVersion)) {
                Platform.runLater(() -> showUpdateDialog(latestVersion));
            }
        } catch (IOException e) {
            System.err.println("Error al comprobar actualizaciones: " + e.getMessage());
        }
    }

    /**
     * Obtiene la última versión disponible desde el servidor
     */
    private String getLatestVersion() throws IOException {
        URL url = new URL(VERSION_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.readLine().trim();
        }
    }

    /**
     * Comprueba si la versión del servidor es más reciente que la actual
     */
    private boolean isNewerVersion(String latestVersion) {
        // Implementación simple de comparación de versiones (supone formato X.Y.Z)
        String[] current = CURRENT_VERSION.split("\\.");
        String[] latest = latestVersion.split("\\.");

        for (int i = 0; i < Math.min(current.length, latest.length); i++) {
            int c = Integer.parseInt(current[i]);
            int l = Integer.parseInt(latest[i]);
            if (l > c) return true;
            if (c > l) return false;
        }

        return latest.length > current.length;
    }

    /**
     * Muestra el diálogo de actualización disponible
     */
    private void showUpdateDialog(String newVersion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualización disponible");
        alert.setHeaderText("¡Hay una nueva versión disponible!");

        VBox content = new VBox(10);
        content.getChildren().add(new Label(
                "Versión actual: " + CURRENT_VERSION + "\n" +
                        "Nueva versión: " + newVersion + "\n\n" +
                        "¿Deseas descargar la nueva versión ahora?"));

        Hyperlink link = new Hyperlink("Descargar actualización");
        link.setOnAction(e -> openBrowser(DOWNLOAD_URL));
        content.getChildren().add(link);

        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            openBrowser(DOWNLOAD_URL);
        }
    }

    /**
     * Abre el navegador con la URL especificada
     */
    private void openBrowser(String url) {
        try {
            new ProcessBuilder("cmd", "/c", "start", url).start();
        } catch (IOException e) {
            System.err.println("Error al abrir el navegador: " + e.getMessage());
        }
    }
}