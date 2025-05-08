package com.example.cannagrow;

import com.example.model.Session;
import com.example.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private Label bienvenidaLabel;

    @FXML
    private Button carritoButton;

    @FXML
    private Button pedidosButton;

    @FXML
    private Button productosButton;

    @FXML
    private Button inicioButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView logoImage;

    @FXML
    private Button adminButton;

    @FXML
    private BorderPane mainBorderPane; // ID que pondrás en el BorderPane del menú


    @FXML
    public void initialize() {
        UsuarioModel usuario = Session.getUsuarioActual();

        if (usuario != null) {
            bienvenidaLabel.setText("Bienvenido, " + usuario.getNombre());

            String rol = usuario.getRol().toLowerCase();

            // Cargar logo
            Image image = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
            logoImage.setImage(image);

            // Control de permisos por rol
            switch (rol) {
                case "gerente":
                    // Admin puede ver todo
                    carritoButton.setVisible(true);
                    pedidosButton.setVisible(true);
                    productosButton.setVisible(true);
                    adminButton.setVisible(true);
                    break;
                case "usuario":
                    // Usuario solo puede acceder a ciertas partes
                    carritoButton.setVisible(true);
                    pedidosButton.setVisible(true);
                    productosButton.setVisible(true);
                    adminButton.setVisible(false);
                    break;
                default:
                    // Rol desconocido, ocultar todo por seguridad
                    carritoButton.setVisible(false);
                    pedidosButton.setVisible(false);
                    productosButton.setVisible(false);
                    adminButton.setVisible(false);
                    break;
            }
        }
    }

    @FXML
    private void onMostrarRegistroClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/register-view.fxml"));
            Parent registroVista = loader.load();
            mainBorderPane.setCenter(registroVista);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de registro.");
        }
    }


    @FXML
    private void onInicioClick() {
        mostrarMensaje("Inicio", "Estás en el menú principal.");
    }

    @FXML
    private void onProductosClick() {
        mostrarMensaje("Productos", "Aquí irán los productos disponibles.");
    }

    @FXML
    private void onCarritoClick() {
        mostrarMensaje("Carrito", "Aquí se mostrarán los productos en tu carrito.");
    }

    @FXML
    private void onPedidosClick() {
        mostrarMensaje("Pedidos", "Aquí podrás revisar tus pedidos.");
    }

    @FXML
    private void onLogoutClick(javafx.event.ActionEvent event) {
        mostrarMensaje("Cerrar sesión", "Sesión cerrada. Vuelve pronto.");
        Session.setUsuarioActual(null);
        Session.cerrarSesion();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene("/com/example/cannagrow/hello-view.fxml", stage); // Te lleva de vuelta al login
    }

    @FXML
    private void onAdminClick(javafx.event.ActionEvent event) {
        mostrarMensaje("Admin", "Bienvenido Administrador de Cannagrow.");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene("/com/example/cannagrow/menu-admin.fxml", stage);
    }

    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
