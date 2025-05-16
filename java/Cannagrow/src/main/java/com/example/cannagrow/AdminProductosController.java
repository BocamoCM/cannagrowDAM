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

/**
 * Controlador para la administración de productos.
 * Permite visualizar, buscar y editar la información de los productos del sistema.
 * Facilita la gestión del inventario a los administradores.
 */
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

    /**
     * Inicializa el controlador de administración de productos.
     * Configura las columnas de la tabla, carga los productos iniciales y
     * establece los listeners para la selección de productos.
     */
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

    /**
     * Carga todos los productos desde la base de datos y los muestra en la tabla.
     * Utiliza el modelo ProductoModel para obtener la lista de productos y
     * actualiza la tabla con los datos obtenidos.
     */
    private void cargarProductos() {
        List<Producto> productos = ProductoModel.obtenerTodos();
        productosObservable = FXCollections.observableArrayList(productos);
        tablaProductos.setItems(productosObservable);
    }

    /**
     * Muestra los datos de un producto en los campos de edición.
     * Permite la visualización y edición de la información del producto seleccionado.
     *
     * @param p El producto cuyos datos se mostrarán en los campos de edición.
     */
    private void mostrarProducto(Producto p) {
        idField.setText(String.valueOf(p.getId()));
        nombreField.setText(p.getNombre());
        tipoField.setText(p.getTipo());
        thcField.setText(String.valueOf(p.getContenidoTHC()));
        cbdField.setText(String.valueOf(p.getContenidoCBD()));
        precioField.setText(String.valueOf(p.getPrecio()));
        stockField.setText(String.valueOf(p.getStock()));
    }

    /**
     * Maneja el evento de búsqueda de productos.
     * Filtra los productos según el término de búsqueda introducido por el usuario.
     * Si el campo de búsqueda está vacío, muestra todos los productos.
     *
     * @param event El evento de acción que desencadena la búsqueda.
     */
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

    /**
     * Maneja el evento de guardar cambios en un producto.
     * Recoge los datos de los campos de edición, valida los formatos numéricos y
     * actualiza el producto en la base de datos.
     * Muestra alertas informativas sobre el resultado de la operación.
     *
     * @param event El evento de acción que desencadena el guardado de cambios.
     */
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

    /**
     * Actualiza la información de un producto en la base de datos.
     * Utiliza el modelo ProductoModel para realizar la actualización.
     *
     * @param producto El producto con la información actualizada que se guardará en la base de datos.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    private boolean actualizarProductoEnBD(Producto producto) {
        // Aquí debes implementar la lógica para actualizar el producto en la BD,
        // por ejemplo, con un método estático en ProductoModel:
        return ProductoModel.actualizarProducto(producto);
    }

    /**
     * Recarga todos los productos desde la base de datos.
     * Actualiza la tabla con los datos más recientes de los productos.
     *
     * @param event El evento de acción que desencadena la recarga.
     */
    @FXML
    public void recargarProductos(ActionEvent event) {
        cargarProductos();
    }
}
