package com.example.cannagrow;

import com.example.model.Session;
import com.example.model.UsuarioModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class MenuAdminController {

    @FXML
    private Label bienvenidaLabel;



    @FXML
    private ImageView logoImage;

    @FXML
    private Button usuariosButton; // Nuevo botón para administrar usuarios

    @FXML
    private BorderPane adminBorderPane;
    @FXML
    private Button IniciotButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button pedidoButton;

    @FXML
    public void initialize() {
        UsuarioModel usuario = Session.getUsuarioActual();

        if (usuario != null) {
            bienvenidaLabel.setText("Bienvenido, " + usuario.getNombre());

            String rol = usuario.getRol().toLowerCase();

            // Primero verificamos si los elementos FXML están correctamente inyectados
            if (logoImage == null) {
                System.err.println("ERROR: logoImage es null");
            }


            // Cargar logo primero
            cargarFotoPerfil();

            // Control de permisos por rol
            switch (rol) {
                case "gerente":
                    // Gerente puede ver todo
                    usuariosButton.setVisible(true); // Solo gerentes pueden administrar usuarios
                    break;

                default:
                    // Rol desconocido, ocultar todo por seguridad
                    usuariosButton.setVisible(false);
                    break;
            }
        }
    }


    @FXML
    private void onRegisterClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/register.fxml"));
            Parent registroVista = loader.load();
            adminBorderPane.setCenter(registroVista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onUsuariosClick() {
        try {
            // Cargar la vista de administración de usuarios
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/admin-usuarios.fxml"));
            Parent usuariosVista = loader.load();

            // Ocultar la etiqueta de bienvenida
            bienvenidaLabel.setVisible(false);

            // Mostrar la vista de administración de usuarios en el centro
            adminBorderPane.setCenter(usuariosVista);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la pantalla de administración de usuarios.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onLogoutClick(javafx.event.ActionEvent event) {
        mostrarMensaje("Cerrar sesión", "Sesión cerrada. Vuelve pronto.", Alert.AlertType.WARNING);
        Session.setUsuarioActual(null);
        Session.cerrarSesion();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene("/com/example/cannagrow/hello-view.fxml", stage); // Te lleva de vuelta al login
    }

    private void mostrarMensaje(String titulo, String contenido, Alert.AlertType warning) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void cargarFotoPerfil() {
        try {
            UsuarioModel usuario = Session.getUsuarioActual();

            if (usuario != null && usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().isEmpty()) {
                // Intentar cargar la foto de perfil del usuario
                String fotoPerfilUrl = usuario.getFotoPerfilUrl();
                System.out.println("Intentando cargar foto de perfil desde: " + fotoPerfilUrl);

                InputStream fotoStream = getClass().getResourceAsStream(fotoPerfilUrl);

                if (fotoStream != null) {
                    logoImage.setImage(new Image(fotoStream));
                    // Configurar el ImageView para mostrar la foto en círculo
                    logoImage.setStyle("-fx-background-radius: 50%; -fx-background-color: white;");
                    System.out.println("Foto de perfil cargada correctamente");
                    return;
                } else {
                    System.err.println("No se pudo encontrar la foto de perfil en: " + fotoPerfilUrl);

                    // Si la foto no se encuentra como recurso, intentar cargar como ruta absoluta o URL
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

            // Si no hay usuario logueado o no se pudo cargar la foto, cargar logo por defecto
            cargarLogoPorDefecto();

        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            e.printStackTrace();
            // Si ocurre algún error, intentar cargar el logo por defecto
            cargarLogoPorDefecto();
        }
    }

    private void cargarLogoPorDefecto() {
        try {
            String logoPath = "/com/example/cannagrow/img/perfil_cliente.png";
            System.out.println("Cargando logo por defecto desde: " + logoPath);

            InputStream logoStream = getClass().getResourceAsStream(logoPath);
            if (logoStream != null) {
                logoImage.setImage(new Image(logoStream));
                System.out.println("Logo por defecto cargado correctamente");
            } else {
                // Intentar con la ruta del logo principal si la imagen de perfil falla
                String fallbackPath = "/com/example/cannagrow/cannagrow_logo.png";
                InputStream fallbackStream = getClass().getResourceAsStream(fallbackPath);

                if (fallbackStream != null) {
                    logoImage.setImage(new Image(fallbackStream));
                    System.out.println("Logo principal cargado como alternativa");
                } else {
                    System.err.println("No se pudo cargar ningún logo por defecto");
                }
            }

            // Resetear el estilo para logos no circulares
            logoImage.setStyle("");
        } catch (Exception e) {
            System.err.println("Error al cargar el logo por defecto: " + e.getMessage());
        }
    }


    @FXML
    public void onInicioClick(ActionEvent actionEvent) {
        try {
            // Verificar si el usuario tiene permiso de administrador
            UsuarioModel usuario = Session.getUsuarioActual();
            if (usuario == null || usuario.getRol() == null || !usuario.getRol().equalsIgnoreCase("gerente")) {
                mostrarMensaje("Acceso denegado",
                        "No tienes permisos para acceder al panel de administración.",
                        Alert.AlertType.WARNING);
                return;
            }

            mostrarMensaje("Admin", "Bienvenido Administrador de Cannagrow.", Alert.AlertType.WARNING);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            SceneChanger.changeScene("/com/example/cannagrow/menu.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo acceder al panel de administración.", Alert.AlertType.WARNING);
        }

    }

    @FXML
    public void onPedidoClick(ActionEvent actionEvent) {
        try {
            // Cargar la vista de administración de usuarios
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/admin-pedidos.fxml"));
            Parent usuariosVista = loader.load();

            // Ocultar la etiqueta de bienvenida
            bienvenidaLabel.setVisible(false);

            // Mostrar la vista de administración de usuarios en el centro
            adminBorderPane.setCenter(usuariosVista);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la pantalla de administración de usuarios.", Alert.AlertType.WARNING);
        }
    }
}

