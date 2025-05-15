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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Optional;

public class AdminPedidosController implements Initializable {

    @FXML
    private TableView<Pedido> pedidosTableView;

    @FXML
    private TableColumn<Pedido, Integer> idColumn;

    @FXML
    private TableColumn<Pedido, Date> fechaColumn;

    @FXML
    private TableColumn<Pedido, String> clienteColumn;

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

    @FXML
    private ComboBox<EstadoPedido> cambioEstadoComboBox;

    @FXML
    private Button btnRefrescar;

    @FXML
    private Button btnCambiarEstado;

    @FXML
    private Button btnAsignarEmpleado;

    private ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private ObservableList<DetallePedido> listaDetalles = FXCollections.observableArrayList();
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private Pedido pedidoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Verificar si el usuario es administrador
        verificarPermisos();

        // Configurar la tabla de pedidos
        configurarTablaPedidos();

        // Configurar el filtro de estados
        configurarFiltroEstados();

        // Configurar el combo de cambio de estado
        configurarCambioEstado();

        // Configurar la tabla de detalles
        configurarTablaDetalles();

        // Ocultar el panel de detalles al inicio
        detallesPedidoPane.setVisible(false);

        // Cargar todos los pedidos al inicio
        cargarTodosPedidos();
    }

    private void verificarPermisos() {
        UsuarioModel usuario = Session.getUsuarioActual();
        if (usuario == null || (!usuario.getRol().equalsIgnoreCase("gerente") && !usuario.getRol().equalsIgnoreCase("administrador"))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acceso Restringido");
            alert.setHeaderText("No tiene permisos para acceder a esta sección");
            alert.setContentText("Esta sección está reservada para administradores y gerentes.");
            alert.showAndWait();

            // Aquí podríamos redirigir a otra pantalla, pero dejaremos que siga para no bloquear la vista
        }
    }

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

        clienteColumn.setCellValueFactory(cellData -> {
            Pedido pedido = cellData.getValue();
            if (pedido == null) return new SimpleStringProperty("Desconocido");
            String nombreCliente = PedidoAdminModel.obtenerNombreClientePorId(pedido.getClienteId());
            return new SimpleStringProperty(nombreCliente);
        });


        clienteColumn.setCellValueFactory(cellData -> {
            Pedido pedido = cellData.getValue();
            String nombreCliente = PedidoAdminModel.obtenerNombreClientePorId(pedido.getClienteId());
            return new SimpleStringProperty(nombreCliente);
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
                        setStyle("");
                    } else {
                        setText(estado.getEstado());
                        // Asignar colores según el estado
                        switch (estado) {
                            case PENDIENTE:
                                setStyle("-fx-text-fill: orange;");
                                break;
                            case ENVIADO:
                                setStyle("-fx-text-fill: blue;");
                                break;
                            case ENTREGADO:
                                setStyle("-fx-text-fill: green;");
                                break;
                            case CANCELADO:
                                setStyle("-fx-text-fill: red;");
                                break;
                            default:
                                setStyle("");
                                break;
                        }
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

    private void configurarColumnaBotones() {
        Callback<TableColumn<Pedido, Void>, TableCell<Pedido, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Pedido, Void> call(final TableColumn<Pedido, Void> param) {
                return new TableCell<>() {
                    private final Button verDetallesBtn = new Button("Ver Detalles");
                    private final HBox pane = new HBox(5);

                    {
                        verDetallesBtn.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
                        verDetallesBtn.setOnAction((ActionEvent event) -> {
                            Pedido pedido = getTableView().getItems().get(getIndex());
                            pedidoSeleccionado = pedido;
                            mostrarDetallesPedido(pedido);
                        });

                        pane.getChildren().add(verDetallesBtn);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };

        accionesColumn.setCellFactory(cellFactory);
    }

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

    private void configurarCambioEstado() {
        // Configurar las opciones del combo de cambio de estado
        ObservableList<EstadoPedido> opcionesEstado = FXCollections.observableArrayList(EstadoPedido.values());
        cambioEstadoComboBox.setItems(opcionesEstado);

        // Configurar el convertidor para mostrar los estados correctamente
        cambioEstadoComboBox.setConverter(new StringConverter<EstadoPedido>() {
            @Override
            public String toString(EstadoPedido estado) {
                return estado != null ? estado.getEstado() : "";
            }

            @Override
            public EstadoPedido fromString(String string) {
                for (EstadoPedido estado : EstadoPedido.values()) {
                    if (estado.getEstado().equals(string)) {
                        return estado;
                    }
                }
                return null;
            }
        });
    }

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

    private void cargarTodosPedidos() {
        // Cargar todos los pedidos
        List<Pedido> pedidos = PedidoAdminModel.obtenerTodosPedidos(1, 30);
        listaPedidos.clear();
        listaPedidos.addAll(pedidos);
    }

    private void filtrarPedidosPorEstado() {
        String filtro = filtroEstadoComboBox.getValue();

        List<Pedido> pedidos;

        if ("Todos".equals(filtro)) {
            pedidos = PedidoAdminModel.obtenerTodosPedidos(1, 30);
        } else {
            // Convertir el string del filtro a enum EstadoPedido
            EstadoPedido estadoFiltro = null;
            for (EstadoPedido estado : EstadoPedido.values()) {
                if (estado.getEstado().equals(filtro)) {
                    estadoFiltro = estado;
                    break;
                }
            }

            pedidos = PedidoAdminModel.obtenerPedidosPorEstado(estadoFiltro);
        }

        listaPedidos.clear();
        listaPedidos.addAll(pedidos);
    }

    // 2. Corregir el método mostrarDetallesPedido para cargar correctamente los productos
    private void mostrarDetallesPedido(Pedido pedido) {
        // Cargar los detalles del pedido
        List<DetallePedido> detalles;
        try {
            detalles = PedidoModel.obtenerDetallesPedido(pedido.getId());
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los detalles", e.getMessage(), Alert.AlertType.ERROR);
            return;
        }


        // Cargar la información de productos para cada detalle
        for (DetallePedido detalle : detalles) {
            int productoId = detalle.getProductoId();
            Producto producto = ProductoModel.obtenerPorId(productoId);
            if (producto == null) {
                producto = new Producto(); // vacío o con valores por defecto
                producto.setNombre("Producto desconocido");
            }
            detalle.setProducto(producto);

        }

        // Obtener el nombre del cliente
        String nombreCliente = PedidoAdminModel.obtenerNombreClientePorId(pedido.getClienteId());


        // Actualizar la información del pedido en la etiqueta
        pedidoInfoLabel.setText(String.format(
                "Pedido #%d - Cliente: %s - Fecha: %s - Total: %s",
                pedido.getId(),
                nombreCliente,
                formatoFecha.format(pedido.getFecha()),
                formatoMoneda.format(pedido.getTotal())
        ));

        // Seleccionar el estado actual en el combobox
        cambioEstadoComboBox.setValue(pedido.getEstado());

        // Actualizar la tabla de detalles
        listaDetalles.clear();
        listaDetalles.addAll(detalles);

        // Mostrar el panel de detalles
        detallesPedidoPane.setVisible(true);
    }

    @FXML
    private void onRefrescarClick() {
        cargarTodosPedidos();
    }

    @FXML
    private void onCerrarDetallesClick() {
        // Ocultar el panel de detalles
        detallesPedidoPane.setVisible(false);
        pedidoSeleccionado = null;
    }

    @FXML
    private void onCambiarEstadoClick() {
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "No hay pedido seleccionado",
                    "Seleccione un pedido para cambiar su estado.", Alert.AlertType.ERROR);
            return;
        }

        EstadoPedido nuevoEstado = cambioEstadoComboBox.getValue();
        if (nuevoEstado == null) {
            mostrarAlerta("Error", "No se ha seleccionado un estado",
                    "Seleccione un estado para continuar.", Alert.AlertType.ERROR);
            return;
        }

        // Confirmar cambio de estado
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio");
        confirmacion.setHeaderText("Cambiar estado del pedido #" + pedidoSeleccionado.getId());
        confirmacion.setContentText("¿Está seguro que desea cambiar el estado de '" +
                pedidoSeleccionado.getEstado().getEstado() + "' a '" +
                nuevoEstado.getEstado() + "'?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean actualizado = PedidoAdminModel.actualizarEstadoPedido(pedidoSeleccionado.getId(), nuevoEstado);

            if (actualizado) {
                mostrarAlerta("Éxito", "Estado actualizado",
                        "El estado del pedido ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);

                // Actualizar la vista
                pedidoSeleccionado.setEstado(nuevoEstado);
                pedidosTableView.refresh();

                // Si se cancela el pedido, actualizar el stock
                if (nuevoEstado == EstadoPedido.CANCELADO) {
                    PedidoAdminModel.devolverStockPedidoCancelado(pedidoSeleccionado.getId());
                }
            } else {
                mostrarAlerta("Error", "Error al actualizar",
                        "No se pudo actualizar el estado del pedido.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void onAsignarEmpleadoClick() {
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "No hay pedido seleccionado",
                    "Seleccione un pedido para asignar un empleado.", Alert.AlertType.ERROR);
            return;
        }

        // Obtener lista de empleados disponibles
        List<UsuarioModel> empleados = PedidoAdminModel.obtenerEmpleadosDisponibles();

        if (empleados.isEmpty()) {
            mostrarAlerta("Advertencia", "No hay empleados disponibles",
                    "No hay empleados disponibles para asignar.", Alert.AlertType.WARNING);
            return;
        }

        // Crear un diálogo para seleccionar empleado
        Dialog<UsuarioModel> dialog = new Dialog<>();
        dialog.setTitle("Asignar Empleado");
        dialog.setHeaderText("Seleccione un empleado para el pedido #" + pedidoSeleccionado.getId());

        // Configurar botones
        ButtonType seleccionarButtonType = new ButtonType("Asignar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(seleccionarButtonType, ButtonType.CANCEL);

        // Crear lista de empleados
        ComboBox<UsuarioModel> comboEmpleados = new ComboBox<>();
        comboEmpleados.setItems(FXCollections.observableArrayList(empleados));
        comboEmpleados.setConverter(new StringConverter<UsuarioModel>() {
            @Override
            public String toString(UsuarioModel empleado) {
                return empleado == null ? "" : empleado.getNombre() + " (" + empleado.getRol() + ")";
            }

            @Override
            public UsuarioModel fromString(String string) {
                return null; // No es necesario para este caso
            }
        });

        VBox content = new VBox(10);
        content.getChildren().add(new Label("Empleado:"));
        content.getChildren().add(comboEmpleados);

        dialog.getDialogPane().setContent(content);

        // Convertir el resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == seleccionarButtonType) {
                return comboEmpleados.getValue();
            }
            return null;
        });

        Optional<UsuarioModel> resultado = dialog.showAndWait();
        resultado.ifPresent(empleado -> {
            boolean asignado = PedidoAdminModel.asignarEmpleadoAPedido(pedidoSeleccionado.getId(), empleado.getId());

            if (asignado) {
                mostrarAlerta("Éxito", "Empleado asignado",
                        "El empleado " + empleado.getNombre() + " ha sido asignado al pedido #" +
                                pedidoSeleccionado.getId() + " correctamente.", Alert.AlertType.INFORMATION);

                // Refrescar para ver cambios
                onRefrescarClick();
            } else {
                mostrarAlerta("Error", "Error al asignar empleado",
                        "No se pudo asignar el empleado al pedido.", Alert.AlertType.ERROR);
            }
        });
    }

    private void mostrarAlerta(String titulo, String cabecera, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}