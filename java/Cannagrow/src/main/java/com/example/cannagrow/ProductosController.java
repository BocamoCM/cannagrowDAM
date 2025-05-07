package com.example.cannagrow;

import com.example.model.Producto;
import com.example.model.ProductoModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.util.List;

public class ProductosController {

    @FXML
    private FlowPane contenedorProductos;

    @FXML
    public void initialize() {
        List<Producto> productos = ProductoModel.obtenerTodos();

        for (Producto p : productos) {
            VBox tarjeta = crearTarjetaProducto(p);
            contenedorProductos.getChildren().add(tarjeta);
        }
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox box = new VBox(5);
        box.setStyle("-fx-background-color: #1f1f1f; -fx-padding: 10; -fx-background-radius: 10;");
        box.setPrefWidth(160);
        box.setPrefHeight(220);
        box.setMaxWidth(160);

        ImageView imagen = new ImageView();
        File file;

        if (producto.getImagenProducto() != null && !producto.getImagenProducto().isBlank()) {
            file = new File("src/main/resources/img/" + producto.getImagenProducto());
            if (!file.exists()) {
                file = new File("src/main/resources/img/sin_imagen.png");
            }
        } else {
            file = new File("src/main/resources/img/sin_imagen.png");
        }

        imagen.setImage(new Image(file.toURI().toString()));
        imagen.setFitWidth(140);
        imagen.setFitHeight(100);
        imagen.setPreserveRatio(true);

        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        nombre.setWrapText(true);

        Label precio = new Label(String.format("€ %.2f", producto.getPrecio()));
        precio.setStyle("-fx-text-fill: lightgreen;");

        box.getChildren().addAll(imagen, nombre, precio);
        return box;
    }

}
