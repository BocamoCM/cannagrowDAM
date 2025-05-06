package com.example.cannagrow;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuController {


    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        Image image = new Image(getClass().getResourceAsStream("/com/example/cannagrow/cannagrow_logo.png"));
        logoImage.setImage(image);
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
    private void onLogoutClick() {
        mostrarMensaje("Cerrar sesión", "Sesión cerrada. Vuelve pronto.");
        // Aquí podrías cambiar a la vista de login, si lo deseas
    }

    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
