package com.example.cannagrow;

import com.example.model.Session;
import com.example.model.UsuarioModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MenuAdminController {
    @FXML
    private Button registerButton;
    @FXML
    private Button pedidoButton;
    @FXML
    private Button usuariosButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button inicioButton;
    @FXML
    private ImageView logoImage;
    @FXML
    private BorderPane adminBorderPane;
    @FXML
    private Label bienvenidaLabel;
    @FXML
    private Button productosButton;
    // Cache para las vistas del panel de administración
    private static Parent usuariosPaneCache = null;
    private static Parent registroPaneCache = null;
    private static Parent pedidosPaneCache = null;
    private static Parent productosPaneCache = null;
    @FXML
    public void initialize() {
        System.out.println("Iniciando inicialización de MenuAdminController...");


        // Verificar componentes clave antes de continuar
        boolean componentesValidos = verificarComponentesIU();
        if (!componentesValidos) {
            System.err.println("Algunos componentes de la UI de administración no están disponibles. La inicialización será parcial.");
        }

        // Cargar datos del usuario actual (administrador)
        UsuarioModel usuario = Session.getUsuarioActual();
        if (usuario != null) {
            // Cargar foto de perfil si el componente está disponible
            cargarFotoPerfil();

            // Verificar que es administrador/gerente
            verificarPermisos(usuario);
        } else {
            System.out.println("No hay usuario logueado en el panel de administración");
            volverAInicio();
        }

        // Aplicar estilos iniciales a los botones
        applyButtonStyles();

        System.out.println("Inicialización de MenuAdminController completada");
    }

    /**
     * Aplica los estilos iniciales a los botones
     */
    private void applyButtonStyles() {
        // Estilo para botones normales
        String normalStyle = "-fx-background-color: #555555; -fx-text-fill: white;";
        // Estilo para botones de acción negativa
        String negativeStyle = "-fx-background-color: #d32f2f; -fx-text-fill: white;";

        if (registerButton != null) registerButton.setStyle(normalStyle);
        if (pedidoButton != null) pedidoButton.setStyle(normalStyle);
        if (usuariosButton != null) usuariosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
        if (logoutButton != null) logoutButton.setStyle(negativeStyle);
        if (inicioButton != null) inicioButton.setStyle(negativeStyle);
    }

    /**
     * Verifica que los componentes críticos de la UI existan
     */
    private boolean verificarComponentesIU() {
        boolean todosDisponibles = true;

        if (adminBorderPane == null) {
            System.err.println("ERROR: adminBorderPane es null");
            todosDisponibles = false;
        }

        if (logoImage == null) {
            System.err.println("ERROR: logoImage es null");
            todosDisponibles = false;
        }

        return todosDisponibles;
    }

    /**
     * Verifica que el usuario actual tenga permisos de administrador
     */
    private void verificarPermisos(UsuarioModel usuario) {
        if (usuario == null) {
            volverAInicio();
            return;
        }

        String rol = (usuario.getRol() != null) ? usuario.getRol().toLowerCase() : "";

        if (!rol.equals("gerente") && !rol.equals("admin")) {
            mostrarMensaje("Acceso denegado",
                    "No tienes permisos para acceder al panel de administración.",
                    Alert.AlertType.ERROR);
            volverAInicio();
        } else if (bienvenidaLabel != null) {
            bienvenidaLabel.setText("Bienvenido al panel de administración de CannaGrow, " + usuario.getNombre());
        }
    }

    /**
     * Vuelve a la vista principal cuando el usuario no es administrador
     */
    private void volverAInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu.fxml"));
            Parent root = loader.load();

            // Si estamos en un contexto de UI válido, cambiamos de vista
            if (adminBorderPane != null && adminBorderPane.getScene() != null) {
                Scene scene = adminBorderPane.getScene();
                scene.setRoot(root);
            } else {
                System.err.println("No se puede volver al inicio: contexto de UI no válido");
            }
        } catch (IOException e) {
            System.err.println("Error al volver a la vista de inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarFotoPerfil() {
        try {
            // Verificar que el componente existe antes de manipularlo
            if (logoImage == null) {
                System.err.println("ERROR: No se puede cargar la foto de perfil porque logoImage es null");
                return;
            }

            UsuarioModel usuario = Session.getUsuarioActual();

            if (usuario != null && usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().isEmpty()) {
                String fotoPerfilUrl = usuario.getFotoPerfilUrl();
                System.out.println("Intentando cargar foto de perfil desde: " + fotoPerfilUrl);

                InputStream fotoStream = getClass().getResourceAsStream(fotoPerfilUrl);

                if (fotoStream != null) {
                    logoImage.setImage(new Image(fotoStream));
                    logoImage.setStyle("-fx-background-radius: 50%; -fx-background-color: white;");
                    System.out.println("Foto de perfil cargada correctamente");
                    return;
                } else {
                    System.err.println("No se pudo encontrar la foto de perfil en: " + fotoPerfilUrl);

                    try {
                        Image imagen = new Image(fotoPerfilUrl);
                        if (!imagen.isError()) {
                            logoImage.setImage(imagen);
                            logoImage.setStyle("-fx-background-radius: 50%; -fx-background-color: white;");
                            System.out.println("Foto de perfil cargada desde ruta absoluta o URL");
                            return;
                        }
                    } catch (Exception ex) {
                        System.err.println("Error al cargar desde ruta absoluta: " + ex.getMessage());
                    }
                }
            }

            cargarLogoPorDefecto();

        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            e.printStackTrace();
            cargarLogoPorDefecto();
        }
    }

    private void cargarLogoPorDefecto() {
        try {
            // Verificar que el componente existe antes de manipularlo
            if (logoImage == null) {
                System.err.println("ERROR: No se puede cargar el logo por defecto porque logoImage es null");
                return;
            }

            String logoPath = "/com/example/cannagrow/img/admin_profile.png";
            System.out.println("Cargando logo administrador por defecto desde: " + logoPath);

            InputStream logoStream = getClass().getResourceAsStream(logoPath);
            if (logoStream != null) {
                logoImage.setImage(new Image(logoStream));
                System.out.println("Logo por defecto cargado correctamente");
            } else {
                String fallbackPath = "/com/example/cannagrow/cannagrow_logo.png";
                InputStream fallbackStream = getClass().getResourceAsStream(fallbackPath);

                if (fallbackStream != null) {
                    logoImage.setImage(new Image(fallbackStream));
                    System.out.println("Logo principal cargado como alternativa");
                } else {
                    System.err.println("No se pudo cargar ningún logo por defecto");
                }
            }

            logoImage.setStyle("");
        } catch (Exception e) {
            System.err.println("Error al cargar el logo por defecto: " + e.getMessage());
        }
    }

    private void resetearBotonesMenu() {
        String estiloNormal = "-fx-background-color: #555555; -fx-text-fill: white;";
        if (registerButton != null) registerButton.setStyle(estiloNormal);
        if (pedidoButton != null) pedidoButton.setStyle(estiloNormal);
        if (usuariosButton != null) usuariosButton.setStyle(estiloNormal);

        // Mantener los botones de acción negativa con su estilo
        String negativeStyle = "-fx-background-color: #d32f2f; -fx-text-fill: white;";
        if (logoutButton != null) logoutButton.setStyle(negativeStyle);
        if (inicioButton != null) inicioButton.setStyle(negativeStyle);
    }

    @FXML
    public void onRegisterClick(ActionEvent event) {
        try {
            resetearBotonesMenu();
            registerButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Implementación para cargar la vista de registro de usuarios
            if (registroPaneCache == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/registro-usuario.fxml"));
                registroPaneCache = loader.load();
            }

            if (adminBorderPane != null) {
                adminBorderPane.setCenter(registroPaneCache);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de registro: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de registro de usuarios: " + e.getMessage());
        }
    }

    @FXML
    public void onPedidoClick(ActionEvent event) {
        try {
            resetearBotonesMenu();
            pedidoButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Implementación para cargar la vista de administración de pedidos
            if (pedidosPaneCache == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/admin-pedidos.fxml"));
                pedidosPaneCache = loader.load();
            }

            if (adminBorderPane != null) {
                adminBorderPane.setCenter(pedidosPaneCache);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de pedidos: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de administración de pedidos: " + e.getMessage());
        }
    }

    @FXML
    public void onUsuariosClick(ActionEvent event) {
        try {
            resetearBotonesMenu();
            usuariosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Implementación para cargar la vista de administración de usuarios
            if (usuariosPaneCache == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/admin-usuarios.fxml"));
                usuariosPaneCache = loader.load();
            }

            if (adminBorderPane != null) {
                adminBorderPane.setCenter(usuariosPaneCache);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de usuarios: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de administración de usuarios: " + e.getMessage());
        }
    }

    @FXML
    public void onLogoutClick(ActionEvent event) {
        try {
            Session.cerrarSesion();
            // Cerrar la ventana actual
            if (logoutButton != null) {
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();
            } else {
                System.err.println("ERROR: No se puede hacer logout porque logoutButton es null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "Error al cerrar sesión: " + e.getMessage());
        }
    }

    @FXML
    public void onInicioClick(ActionEvent event) {
        try {
            // Cargar el menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual
            Stage stage = (Stage) inicioButton.getScene().getWindow();

            // Establecer la nueva escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "Error al volver al menú principal: " + e.getMessage());
        }
    }


    public void mostrarMensaje(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.INFORMATION);
    }

    public void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    public void onProductosClick(ActionEvent event) {
        try {
            resetearBotonesMenu();
            productosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            if (productosPaneCache == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/admin-productos.fxml"));
                productosPaneCache = loader.load();
            }

            if (adminBorderPane != null) {
                adminBorderPane.setCenter(productosPaneCache);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de productos: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de administración de productos: " + e.getMessage());
        }
    }


}
