package com.example.cannagrow;

import com.example.model.*;
import com.example.model.PedidoModel.Pedido;
import com.example.model.PedidoModel.DetallePedido;
import com.example.model.PedidoModel.EstadoPedido;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador de la vista de pedidos del cliente en la aplicación.
 * Gestiona la visualización, filtrado y detalle de los pedidos realizados.
 */
public class PedidosController implements Initializable {

    @FXML
    private TableView<Pedido> pedidosTableView;

    @FXML
    private TableColumn<Pedido, Integer> idColumn;

    @FXML
    private TableColumn<Pedido, Date> fechaColumn;

    @FXML
    private TableColumn<Pedido, Float> totalColumn;

    @FXML
    private TableColumn<Pedido, EstadoPedido> estadoColumn;

    @FXML
    private TableColumn<Pedido, String> vehiculoColumn;

    @FXML
    private TableColumn<Pedido, Void> accionesColumn;

    @FXML
    private VBox detallesPedidoPane;

    @FXML
    private Label pedidoInfoLabel;

    @FXML
    private TableView<DetallePedido> detallesTableView;

    @FXML
    private TableColumn<DetallePedido, String> productoColumn;

    @FXML
    private TableColumn<DetallePedido, Integer> cantidadColumn;

    @FXML
    private TableColumn<DetallePedido, Float> precioColumn;

    @FXML
    private TableColumn<DetallePedido, Float> subtotalColumn;

    @FXML
    private ComboBox<String> filtroEstadoComboBox;

    private ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private ObservableList<DetallePedido> listaDetalles = FXCollections.observableArrayList();
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Método de inicialización llamado automáticamente al cargar el controlador.
     * Configura tablas, filtros y carga los pedidos del usuario actual.
     *
     * @param url no se utiliza.
     * @param resourceBundle no se utiliza.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar la tabla de pedidos
        configurarTablaPedidos();

        // Configurar el filtro de estados
        configurarFiltroEstados();

        // Configurar la tabla de detalles
        configurarTablaDetalles();

        // Cargar los pedidos del usuario actual
        cargarPedidosUsuario();
    }

    /**
     * Configura las columnas y celdas de la tabla de pedidos.
     */
    private void configurarTablaPedidos() {
        // Configurar las columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaColumn.setCellFactory(column -> {
            return new TableCell<Pedido, Date>() {
                @Override
                protected void updateItem(Date fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (empty || fecha == null) {
                        setText(null);
                    } else {
                        setText(formatoFecha.format(fecha));
                    }
                }
            };
        });

        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setCellFactory(column -> {
            return new TableCell<Pedido, Float>() {
                @Override
                protected void updateItem(Float total, boolean empty) {
                    super.updateItem(total, empty);
                    if (empty || total == null) {
                        setText(null);
                    } else {
                        setText(formatoMoneda.format(total));
                    }
                }
            };
        });

        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estadoColumn.setCellFactory(column -> {
            return new TableCell<Pedido, EstadoPedido>() {
                @Override
                protected void updateItem(EstadoPedido estado, boolean empty) {
                    super.updateItem(estado, empty);
                    if (empty || estado == null) {
                        setText(null);
                    } else {
                        setText(estado.getEstado());
                    }
                }
            };
        });

        vehiculoColumn.setCellValueFactory(new PropertyValueFactory<>("vehiculoMatricula"));

        // Configurar la columna de acciones con botones
        configurarColumnaBotones();

        // Asignar los datos a la tabla
        pedidosTableView.setItems(listaPedidos);
    }

    /**
     * Agrega botones personalizados en la columna de acciones de la tabla de pedidos.
     */
    private void configurarColumnaBotones() {
        Callback<TableColumn<Pedido, Void>, TableCell<Pedido, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Pedido, Void> call(final TableColumn<Pedido, Void> param) {
                return new TableCell<>() {
                    private final Button verDetallesBtn = new Button("Ver Detalles");

                    {
                        verDetallesBtn.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
                        verDetallesBtn.setOnAction((ActionEvent event) -> {
                            Pedido pedido = getTableView().getItems().get(getIndex());
                            mostrarDetallesPedido(pedido);
                        });
                    }
                    /**
                     * Actualizar item
                     */
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(verDetallesBtn);
                        }
                    }
                };
            }
        };

        accionesColumn.setCellFactory(cellFactory);
    }

    /**
     * Configura el ComboBox de filtro de estados de pedido.
     */
    private void configurarFiltroEstados() {
        // Agregar la opción "Todos" y los estados disponibles
        ObservableList<String> opcionesFiltro = FXCollections.observableArrayList();
        opcionesFiltro.add("Todos");

        for (EstadoPedido estado : EstadoPedido.values()) {
            opcionesFiltro.add(estado.getEstado());
        }

        filtroEstadoComboBox.setItems(opcionesFiltro);
        filtroEstadoComboBox.getSelectionModel().selectFirst();

        // Configurar el evento de cambio del filtro
        filtroEstadoComboBox.setOnAction(event -> filtrarPedidosPorEstado());
    }

    /**
     * Configura las columnas y el contenido de la tabla de detalles del pedido.
     */
    private void configurarTablaDetalles() {
        productoColumn.setCellValueFactory(cellData -> {
            DetallePedido detalle = cellData.getValue();
            Producto producto = detalle.getProducto();
            return new SimpleStringProperty(producto != null ? producto.getNombre() : "");
        });

        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        precioColumn.setCellFactory(column -> {
            return new TableCell<DetallePedido, Float>() {
                @Override
                protected void updateItem(Float precio, boolean empty) {
                    super.updateItem(precio, empty);
                    if (empty || precio == null) {
                        setText(null);
                    } else {
                        setText(formatoMoneda.format(precio));
                    }
                }
            };
        });

        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalColumn.setCellFactory(column -> {
            return new TableCell<DetallePedido, Float>() {
                @Override
                protected void updateItem(Float subtotal, boolean empty) {
                    super.updateItem(subtotal, empty);
                    if (empty || subtotal == null) {
                        setText(null);
                    } else {
                        setText(formatoMoneda.format(subtotal));
                    }
                }
            };
        });

        detallesTableView.setItems(listaDetalles);
    }

    /**
     * Carga los pedidos del cliente asociado al usuario actual.
     * Si no hay cliente asociado, se muestra un mensaje de error.
     */
    private void cargarPedidosUsuario() {
        // Obtener el ID del usuario actual
        int usuarioId = Session.getUsuarioActual().getId();

        // Obtener el ID del cliente asociado al usuario
        int clienteId = PedidoModel.obtenerClienteIdPorUsuario(usuarioId);

        if (clienteId > 0) {
            // Cargar los pedidos del cliente
            List<Pedido> pedidos = PedidoModel.obtenerPedidosPorCliente(clienteId);
            listaPedidos.clear();
            listaPedidos.addAll(pedidos);
        } else {
            // Mostrar un diálogo de error si no se encuentra el cliente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cliente no encontrado");
            alert.setContentText("No se pudo encontrar un cliente asociado a su cuenta de usuario.");
            alert.showAndWait();
        }
    }

    /**
     * Aplica el filtro de estado seleccionado para mostrar solo los pedidos con ese estado.
     */
    private void filtrarPedidosPorEstado() {
        String filtro = filtroEstadoComboBox.getValue();

        // Obtener el ID del usuario actual
        int usuarioId = Session.getUsuarioActual().getId();

        // Obtener el ID del cliente asociado al usuario
        int clienteId = PedidoModel.obtenerClienteIdPorUsuario(usuarioId);

        if (clienteId > 0) {
            List<Pedido> pedidos;

            if ("Todos".equals(filtro)) {
                pedidos = PedidoModel.obtenerPedidosPorCliente(clienteId);
            } else {
                // Convertir el string del filtro a enum EstadoPedido
                EstadoPedido estadoFiltro = null;
                for (EstadoPedido estado : EstadoPedido.values()) {
                    if (estado.getEstado().equals(filtro)) {
                        estadoFiltro = estado;
                        break;
                    }
                }

                pedidos = PedidoModel.obtenerPedidosPorClienteYEstado(clienteId, estadoFiltro);
            }

            listaPedidos.clear();
            listaPedidos.addAll(pedidos);
        }
    }

    /**
     * Muestra en pantalla los detalles de un pedido específico.
     *
     * @param pedido el pedido del cual se deben mostrar los detalles.
     */
    private void mostrarDetallesPedido(Pedido pedido) {
        // Cargar los detalles del pedido
        List<DetallePedido> detalles = PedidoModel.obtenerDetallesPedido(pedido.getId());

        // Cargar la información de productos para cada detalle
        for (DetallePedido detalle : detalles) {
            Producto producto = ProductoModel.obtenerPorId(detalle.getProductoId());
            detalle.setProducto(producto);
        }

        // Actualizar la información del pedido en la etiqueta
        pedidoInfoLabel.setText(String.format(
                "Pedido #%d - Fecha: %s - Total: %s",
                pedido.getId(),
                formatoFecha.format(pedido.getFecha()),
                formatoMoneda.format(pedido.getTotal())
        ));

        // Actualizar la tabla de detalles
        listaDetalles.clear();
        listaDetalles.addAll(detalles);

        // Mostrar el panel de detalles
        detallesPedidoPane.setVisible(true);
    }

    /**
     * Oculta el panel lateral de detalles del pedido.
     */
    @FXML
    private void onCerrarDetallesClick() {
        // Ocultar el panel de detalles
        detallesPedidoPane.setVisible(false);
    }
}