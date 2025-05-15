package com.example.cannagrow;

import com.example.model.Producto;
import com.example.model.ProductoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AdminProductosController {

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Integer> colId;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colTipo;

    @FXML
    private TableColumn<Producto, Float> colTHC;

    @FXML
    private TableColumn<Producto, Float> colCBD;

    @FXML
    private TableColumn<Producto, Float> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TextField buscarField;

    // Campos para editar producto
    @FXML
    private TextField idField;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField tipoField;

    @FXML
    private TextField thcField;

    @FXML
    private TextField cbdField;

    @FXML
    private TextField precioField;

    @FXML
    private TextField stockField;

    @FXML
    private Button guardarButton;

    private ObservableList<Producto> productosObservable;

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTHC.setCellValueFactory(new PropertyValueFactory<>("contenidoTHC"));
        colCBD.setCellValueFactory(new PropertyValueFactory<>("contenidoCBD"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        cargarProductos();

        // Listener para seleccionar un producto de la tabla y mostrar datos en campos
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                mostrarProducto(nuevo);
            }
        });
    }

    private void cargarProductos() {
        List<Producto> productos = ProductoModel.obtenerTodos();
        productosObservable = FXCollections.observableArrayList(productos);
        tablaProductos.setItems(productosObservable);
    }

    private void mostrarProducto(Producto p) {
        idField.setText(String.valueOf(p.getId()));
        nombreField.setText(p.getNombre());
        tipoField.setText(p.getTipo());
        thcField.setText(String.valueOf(p.getContenidoTHC()));
        cbdField.setText(String.valueOf(p.getContenidoCBD()));
        precioField.setText(String.valueOf(p.getPrecio()));
        stockField.setText(String.valueOf(p.getStock()));
    }

    @FXML
    public void buscarProductos(ActionEvent event) {
        String termino = buscarField.getText().trim();
        List<Producto> resultados;
        if (termino.isEmpty()) {
            resultados = ProductoModel.obtenerTodos();
        } else {
            resultados = ProductoModel.buscarPorNombre(termino);
        }
        productosObservable.setAll(resultados);
    }

    @FXML
    public void guardarCambios(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            String nombre = nombreField.getText();
            String tipo = tipoField.getText();
            float thc = Float.parseFloat(thcField.getText());
            float cbd = Float.parseFloat(cbdField.getText());
            float precio = Float.parseFloat(precioField.getText());
            int stock = Integer.parseInt(stockField.getText());

            Producto p = new Producto(id, nombre, tipo, thc, cbd, precio, stock, null);

            boolean actualizado = actualizarProductoEnBD(p);
            if (actualizado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
                alert.showAndWait();
                cargarProductos();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al actualizar el producto.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Datos inválidos: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean actualizarProductoEnBD(Producto producto) {
        // Aquí debes implementar la lógica para actualizar el producto en la BD,
        // por ejemplo, con un método estático en ProductoModel:
        return ProductoModel.actualizarProducto(producto);
    }

    @FXML
    public void recargarProductos(ActionEvent event) {
        cargarProductos();
    }
}
