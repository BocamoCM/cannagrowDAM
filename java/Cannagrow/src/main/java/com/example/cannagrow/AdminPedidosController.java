package com.example.cannagrow;

import com.example.model.*;
import com.example.model.PedidoModel.Pedido;
import com.example.model.PedidoModel.DetallePedido;
import com.example.model.PedidoModel.EstadoPedido;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Controlador para la gestión administrativa de pedidos.
 * Permite visualizar, filtrar y gestionar los pedidos del sistema.
 * Implementa carga asíncrona y paginación para mejorar el rendimiento con grandes volúmenes de datos.
 */
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

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    private ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private ObservableList<DetallePedido> listaDetalles = FXCollections.observableArrayList();
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private Pedido pedidoSeleccionado;

    // Configuración para paginación y carga asíncrona
    private static final int LIMITE_CARGA_INICIAL = 15; // Cantidad inicial de pedidos a cargar
    private static final int TAMANO_PAGINA = 10; // Cantidad de pedidos por página adicional
    private int paginaActual = 1;
    private AtomicBoolean estaCargando = new AtomicBoolean(false); // Corregido el error tipográfico
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String filtroActual = "Todos";
    private EstadoPedido estadoFiltrado = null;



    // Campos FXML y variables de clase omitidas para brevedad...

    /**
     * Inicializa el controlador de administración de pedidos.
     * Configura las tablas, filtros, y carga inicial de datos.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz.
     * @param resourceBundle El recurso usado para localizar el objeto raíz.
     */
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

        // Configurar paginación basada en scroll
        configurarPaginacion();



        // Ocultar el panel de detalles al inicio
        detallesPedidoPane.setVisible(false);

        // Inicialmente ocultar la barra de progreso y mensaje de estado
        if (progressBar != null) progressBar.setVisible(false);
        if (statusLabel != null) statusLabel.setVisible(false);

        // Cargar pedidos iniciales de forma asíncrona
        cargarPedidosIniciales();
        configurarEstiloEncabezados();
    }

    /**
     * Configura el estilo de los encabezados de columna
     * para que muestren texto blanco sobre el fondo oscuro.
     */
    private void configurarEstiloEncabezados() {
        // Aplicar estilo a los encabezados de columnas de la tabla principal
        for (TableColumn<?, ?> column : pedidosTableView.getColumns()) {
            column.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        }

        // Aplicar estilo a los encabezados de columnas de la tabla de detalles
        for (TableColumn<?, ?> column : detallesTableView.getColumns()) {
            column.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    /**
     * Verifica que el usuario actual tenga los permisos necesarios para acceder a esta sección.
     * Muestra una alerta si el usuario no es administrador o gerente.
     */
    private void verificarPermisos() {
        UsuarioModel usuario = Session.getUsuarioActual();
        if (usuario == null || (!usuario.getRol().equalsIgnoreCase("gerente") && !usuario.getRol().equalsIgnoreCase("administrador"))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acceso Restringido");
            alert.setHeaderText("No tiene permisos para acceder a esta sección");
            alert.setContentText("Esta sección está reservada para administradores y gerentes.");
            alert.showAndWait();
        }
    }

    /**
     * Configura la tabla principal de pedidos.
     * Define el comportamiento de las columnas y establece optimizaciones de rendimiento.
     * Incluye formato personalizado para fechas, moneda y estados.
     */
    private void configurarTablaPedidos() {
        // Configurar las columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Estilo para la columna de ID
        idColumn.setCellFactory(column -> new TableCell<Pedido, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                this.setStyle("-fx-text-fill: white;");
                setText(empty ? null : id.toString());
            }
        });

        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaColumn.setCellFactory(column -> new TableCell<Pedido, Date>() {
            @Override
            protected void updateItem(Date fecha, boolean empty) {
                super.updateItem(fecha, empty);
                this.setStyle("-fx-text-fill: white;");
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(formatoFecha.format(fecha));
                }
            }
        });

        clienteColumn.setCellValueFactory(cellData -> {
            Pedido pedido = cellData.getValue();
            if (pedido == null) return new SimpleStringProperty("Desconocido");
            // Usar caché para nombres de clientes para mejorar rendimiento
            String nombreCliente = UsuarioCache.getNombreCliente(pedido.getClienteId());
            return new SimpleStringProperty(nombreCliente);
        });

        // Estilo para la columna de cliente
        clienteColumn.setCellFactory(column -> new TableCell<Pedido, String>() {
            @Override
            protected void updateItem(String cliente, boolean empty) {
                super.updateItem(cliente, empty);
                this.setStyle("-fx-text-fill: white;");
                setText(empty ? null : cliente);
            }
        });

        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setCellFactory(column -> new TableCell<Pedido, Float>() {
            @Override
            protected void updateItem(Float total, boolean empty) {
                super.updateItem(total, empty);
                this.setStyle("-fx-text-fill: white;");
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(total));
                }
            }
        });

        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estadoColumn.setCellFactory(column -> new TableCell<Pedido, EstadoPedido>() {
            @Override
            protected void updateItem(EstadoPedido estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("-fx-text-fill: white;");
                } else {
                    setText(estado.getEstado());
                    // Asignar colores según el estado
                    switch (estado) {
                        case PENDIENTE:
                            setStyle("-fx-text-fill: orange;");
                            break;
                        case ENVIADO:
                            setStyle("-fx-text-fill: #38b6ff;"); // Azul más claro para mejor contraste
                            break;
                        case ENTREGADO:
                            setStyle("-fx-text-fill: #5cff5c;"); // Verde más claro para mejor contraste
                            break;
                        case CANCELADO:
                            setStyle("-fx-text-fill: #ff6b6b;"); // Rojo más claro para mejor contraste
                            break;
                        default:
                            setStyle("-fx-text-fill: white;");
                            break;
                    }
                }
            }
        });

        vehiculoColumn.setCellValueFactory(new PropertyValueFactory<>("vehiculoMatricula"));
        vehiculoColumn.setCellFactory(column -> new TableCell<Pedido, String>() {
            @Override
            protected void updateItem(String vehiculo, boolean empty) {
                super.updateItem(vehiculo, empty);
                this.setStyle("-fx-text-fill: white;");
                setText(empty ? null : vehiculo);
            }
        });

        // Configurar la columna de acciones con botones
        configurarColumnaBotones();

        // Asignar los datos a la tabla
        pedidosTableView.setItems(listaPedidos);

        // Optimización: configurar prefetch de 20 filas para mejorar rendimiento de scroll
        pedidosTableView.setFixedCellSize(40); // Altura fija para cada celda
        pedidosTableView.setCache(true); // Activar cache para mejorar rendimiento

        // Establecer estilo para las filas
        pedidosTableView.setRowFactory(tv -> {
            TableRow<Pedido> row = new TableRow<>();
            row.setStyle("-fx-background-color: #1f1f1f;");
            return row;
        });
    }

    /**
     * Configura la columna de botones de acción para cada pedido.
     * Añade un botón "Ver Detalles" que permite mostrar información detallada del pedido seleccionado.
     */
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
                        // Establecer el fondo del contenedor de botones para que coincida con la tabla
                        pane.setStyle("-fx-background-color: transparent;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setStyle("-fx-background-color: #1f1f1f;");
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

    /**
     * Configura el combobox de filtro de estados.
     * Permite filtrar los pedidos según su estado actual (Pendiente, Enviado, Entregado, Cancelado).
     * Incluye una opción "Todos" para mostrar pedidos en cualquier estado.
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
        filtroEstadoComboBox.setOnAction(event -> {
            filtroActual = filtroEstadoComboBox.getValue();
            resetearPaginacion();
            filtrarPedidosPorEstado();
        });
    }

    /**
     * Configura el combobox para cambiar el estado de un pedido.
     * Establece el convertidor para mostrar correctamente los valores de estado.
     */
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

    /**
     * Configura la tabla de detalles de pedido.
     * Define el comportamiento de las columnas para mostrar productos, cantidades,
     * precios unitarios y subtotales.
     */
    private void configurarTablaDetalles() {
        productoColumn.setCellValueFactory(cellData -> {
            DetallePedido detalle = cellData.getValue();
            Producto producto = detalle.getProducto();
            return new SimpleStringProperty(producto != null ? producto.getNombre() : "");
        });

        productoColumn.setCellFactory(column -> new TableCell<DetallePedido, String>() {
            @Override
            protected void updateItem(String producto, boolean empty) {
                super.updateItem(producto, empty);
                this.setStyle("-fx-text-fill: white;");
                setText(empty ? null : producto);
            }
        });

        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cantidadColumn.setCellFactory(column -> new TableCell<DetallePedido, Integer>() {
            @Override
            protected void updateItem(Integer cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                this.setStyle("-fx-text-fill: white;");
                setText(empty ? null : cantidad.toString());
            }
        });

        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        precioColumn.setCellFactory(column -> new TableCell<DetallePedido, Float>() {
            @Override
            protected void updateItem(Float precio, boolean empty) {
                super.updateItem(precio, empty);
                this.setStyle("-fx-text-fill: white;");
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(precio));
                }
            }
        });

        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalColumn.setCellFactory(column -> new TableCell<DetallePedido, Float>() {
            @Override
            protected void updateItem(Float subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                this.setStyle("-fx-text-fill: white;");
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(subtotal));
                }
            }
        });

        detallesTableView.setItems(listaDetalles);

        // Establecer estilo para las filas de la tabla de detalles
        detallesTableView.setRowFactory(tv -> {
            TableRow<DetallePedido> row = new TableRow<>();
            row.setStyle("-fx-background-color: #1f1f1f;");
            return row;
        });
    }

    /**
     * Configura el sistema de paginación basado en scroll.
     * Implementa detección de scroll para cargar más pedidos automáticamente
     * cuando el usuario se acerca al final de la lista.
     */
    private void configurarPaginacion() {
        // Instead of finding the ScrollBar directly at initialization,
        // add a listener to the scene property of the TableView
        pedidosTableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Scene is now set, wait for TableView to be shown and fully rendered
                Platform.runLater(() -> {
                    // Add a scroll event listener directly to the TableView
                    pedidosTableView.addEventFilter(ScrollEvent.ANY, event -> {
                        // If we're scrolling down and not already loading
                        if (event.getDeltaY() < 0 && !estaCargando.get()) { // Corregido el error tipográfico
                            int lastIndex = pedidosTableView.getItems().size() - 1;
                            if (lastIndex >= 0) {
                                // Check if the last item is visible
                                TableRow<?> lastRow = findTableRow(pedidosTableView, lastIndex);
                                if (lastRow != null && lastRow.isVisible()) {
                                    cargarMasPedidos();
                                }

                                // Alternative approach - check if we're close to the end
                                int visibleIndex = getLastVisibleIndex(pedidosTableView);
                                if (visibleIndex >= lastIndex - 3) {
                                    cargarMasPedidos();
                                }
                            }
                        }
                    });

                    // Add a listener for row visibility changes
                    pedidosTableView.skinProperty().addListener((skinObs, oldSkin, newSkin) -> {
                        if (newSkin != null) {
                            // Add a scroll detection mechanism that runs periodically
                            Timeline scrollCheck = new Timeline(
                                    new KeyFrame(Duration.millis(500), e -> {
                                        if (!estaCargando.get() && pedidosTableView.getSkin() != null) { // Corregido el error tipográfico
                                            ScrollBar verticalBar = findScrollBar(pedidosTableView);
                                            if (verticalBar != null) {
                                                double position = verticalBar.getValue() / (verticalBar.getMax() - verticalBar.getMin());
                                                if (position > 0.8) { // User has scrolled more than 80% down
                                                    cargarMasPedidos();
                                                }
                                            }
                                        }
                                    })
                            );
                            scrollCheck.setCycleCount(Timeline.INDEFINITE);
                            scrollCheck.play();
                        }
                    });
                });
            }
        });
    }

    /**
     * Busca la barra de desplazamiento vertical en la tabla de pedidos.
     * Utiliza múltiples estrategias de búsqueda para mayor compatibilidad.
     *
     * @param tableView La tabla donde se buscará la barra de desplazamiento.
     * @return La barra de desplazamiento vertical si se encuentra, null en caso contrario.
     */
    private ScrollBar findScrollBar(TableView<?> tableView) {
        // Try different lookup approaches

        // Approach 1: Direct lookup using CSS selectors
        for (Node node : tableView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) node;
                if (bar.getOrientation() == Orientation.VERTICAL) {
                    return bar;
                }
            }
        }

        // Approach 2: Look for ScrollBar in the parent container
        Parent parent = tableView.getParent();
        while (parent != null) {
            for (Node node : parent.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar) {
                    ScrollBar bar = (ScrollBar) node;
                    if (bar.getOrientation() == Orientation.VERTICAL) {
                        return bar;
                    }
                }
            }
            parent = parent.getParent();
        }

        return null;
    }

    /**
     * Busca una fila específica en la tabla por su índice.
     *
     * @param tableView La tabla donde se buscará la fila.
     * @param index El índice de la fila a buscar.
     * @return La fila encontrada, o null si no se encuentra.
     */
    private TableRow<?> findTableRow(TableView<?> tableView, int index) {
        for (Node node : tableView.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow) {
                TableRow<?> row = (TableRow<?>) node;
                if (row.getIndex() == index) {
                    return row;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene el índice de la última fila visible en la tabla.
     *
     * @param tableView La tabla a examinar.
     * @return El índice de la última fila visible o -1 si no hay filas visibles.
     */
    private int getLastVisibleIndex(TableView<?> tableView) {
        int lastIndex = -1;
        for (Node node : tableView.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow && node.isVisible()) {
                TableRow<?> row = (TableRow<?>) node;
                if (row.getIndex() > lastIndex) {
                    lastIndex = row.getIndex();
                }
            }
        }
        return lastIndex;
    }

    /**
     * Reinicia la paginación al cambiar los filtros.
     * Limpia la lista actual de pedidos y establece la página actual a 1.
     */
    private void resetearPaginacion() {
        // Reiniciar la paginación cuando cambia el filtro
        paginaActual = 1;
        listaPedidos.clear();
    }

    /**
     * Carga los pedidos iniciales de forma asíncrona.
     * Aplica filtros si están configurados y utiliza el sistema de caché para optimizar el rendimiento.
     */
    private void cargarPedidosIniciales() {
        estaCargando.set(true); // Corregido el error tipográfico
        mostrarEstadoCarga(true, "Cargando pedidos iniciales...");

        Task<List<Pedido>> task = new Task<List<Pedido>>() {
            @Override
            protected List<Pedido> call() throws Exception {
                // Determinar si hay un filtro de estado activo
                EstadoPedido estadoFiltro = obtenerEstadoFiltro(filtroActual);

                // Cargar pedidos con paginación
                List<Pedido> pedidos;
                if (estadoFiltro == null) {
                    pedidos = PedidoAdminModel.obtenerTodosPedidos(1, LIMITE_CARGA_INICIAL);
                } else {
                    pedidos = PedidoAdminModel.obtenerPedidosPorEstado(estadoFiltro, 1, LIMITE_CARGA_INICIAL);
                }

                // Pre-cargar datos de clientes para mejorar rendimiento
                for (Pedido pedido : pedidos) {
                    preCargarDatosCliente(pedido.getClienteId());
                }

                return pedidos;
            }
        };

        task.setOnSucceeded(event -> {
            List<Pedido> pedidos = task.getValue();
            Platform.runLater(() -> {
                listaPedidos.addAll(pedidos);
                pedidosTableView.refresh();
                estaCargando.set(false); // Corregido el error tipográfico
                mostrarEstadoCarga(false, "");
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                mostrarAlerta("Error", "Error al cargar pedidos",
                        "No se pudieron cargar los pedidos. Intente nuevamente.", Alert.AlertType.ERROR);
                estaCargando.set(false); // Corregido el error tipográfico
                mostrarEstadoCarga(false, "");
            });
        });

        executorService.submit(task);
    }

    /**
     * Carga más pedidos cuando el usuario se desplaza hacia el final de la lista.
     * Implementa carga asíncrona para no bloquear la interfaz.
     */
    private void cargarMasPedidos() {
        if (estaCargando.get()) return; // Corregido el error tipográfico

        estaCargando.set(true); // Corregido el error tipográfico
        mostrarEstadoCarga(true, "Cargando más pedidos...");

        paginaActual++;

        Task<List<Pedido>> task = new Task<List<Pedido>>() {
            @Override
            protected List<Pedido> call() throws Exception {
                // Determinar si hay un filtro de estado activo
                EstadoPedido estadoFiltro = obtenerEstadoFiltro(filtroActual);

                // Cargar siguiente página de pedidos
                List<Pedido> pedidos;
                if (estadoFiltro == null) {
                    pedidos = PedidoAdminModel.obtenerTodosPedidos(paginaActual, TAMANO_PAGINA);
                } else {
                    pedidos = PedidoAdminModel.obtenerPedidosPorEstado(estadoFiltro, paginaActual, TAMANO_PAGINA);
                }

                // Pre-cargar datos de clientes para mejorar rendimiento
                for (Pedido pedido : pedidos) {
                    preCargarDatosCliente(pedido.getClienteId());
                }

                return pedidos;
            }
        };

        task.setOnSucceeded(event -> {
            List<Pedido> pedidos = task.getValue();
            Platform.runLater(() -> {
                if (pedidos.isEmpty()) {
                    // No hay más pedidos para cargar
                    statusLabel.setText("No hay más pedidos para mostrar");
                } else {
                    listaPedidos.addAll(pedidos);
                    pedidosTableView.refresh();
                }
                estaCargando.set(false); // Corregido el error tipográfico
                mostrarEstadoCarga(false, "");
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                mostrarAlerta("Error", "Error al cargar más pedidos",
                        "No se pudieron cargar más pedidos. Intente nuevamente.", Alert.AlertType.ERROR);
                estaCargando.set(false); // Corregido el error tipográfico
                mostrarEstadoCarga(false, "");
            });
        });

        executorService.submit(task);
    }

    /**
     * Filtra los pedidos según el estado seleccionado en el combobox.
     * Utiliza tareas asíncronas para mantener la interfaz responsiva durante la carga.
     */
    private void filtrarPedidosPorEstado() {
        if (estaCargando.get()) return; // Corregido el error tipográfico

        estaCargando.set(true); // Corregido el error tipográfico
        mostrarEstadoCarga(true, "Aplicando filtro...");

        String filtro = filtroEstadoComboBox.getValue();
        Task<List<Pedido>> task = new Task<List<Pedido>>() {
            @Override
            protected List<Pedido> call() throws Exception {
                List<Pedido> pedidos;
                if ("Todos".equals(filtro)) {
                    pedidos = PedidoAdminModel.obtenerTodosPedidos(1, LIMITE_CARGA_INICIAL);
                } else {
                    // Convertir el string del filtro a enum EstadoPedido
                    EstadoPedido estadoFiltro = null;
                    for (EstadoPedido estado : EstadoPedido.values()) {
                        if (estado.getEstado().equals(filtro)) {
                            estadoFiltro = estado;
                            break;
                        }
                    }

                    pedidos = PedidoAdminModel.obtenerPedidosPorEstado(estadoFiltro, 1, LIMITE_CARGA_INICIAL);
                }

                // Pre-cargar datos de clientes
                for (Pedido pedido : pedidos) {
                    preCargarDatosCliente(pedido.getClienteId());
                }

                return pedidos;
            }
        };

        task.setOnSucceeded(event -> {
            List<Pedido> pedidos = task.getValue();
            Platform.runLater(() -> {
                listaPedidos.clear();
                listaPedidos.addAll(pedidos);
                pedidosTableView.refresh();
                estaCargando.set(false); // Corregido el error tipográfico
                mostrarEstadoCarga(false, "");
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                mostrarAlerta("Error", "Error al aplicar filtro",
                        "No se pudo aplicar el filtro seleccionado. Intente nuevamente.", Alert.AlertType.ERROR);
                estaCargando.set(false); // Corregido el error tipográfico
                mostrarEstadoCarga(false, "");
            });
        });

        executorService.submit(task);
    }

    /**
     * Convierte el valor de texto del filtro al enum correspondiente de EstadoPedido.
     *
     * @param filtro El texto del filtro seleccionado.
     * @return El valor del enum EstadoPedido correspondiente, o null si es "Todos".
     */
    private EstadoPedido obtenerEstadoFiltro(String filtro) {
        if ("Todos".equals(filtro)) {
            return null;
        }

        // Convertir el string del filtro a enum EstadoPedido
        for (EstadoPedido estado : EstadoPedido.values()) {
            if (estado.getEstado().equals(filtro)) {
                return estado;
            }
        }

        return null;
    }

    /**
     * Precarga los datos de un cliente para mejorar el rendimiento.
     * Añade la información del cliente al caché si no está presente.
     *
     * @param clienteId El ID del cliente cuyos datos se precargarán.
     */
    private void preCargarDatosCliente(int clienteId) {
        // Este método asegura que los datos del cliente estén en caché
        if (!UsuarioCache.existeCliente(clienteId)) {
            String nombreCliente = PedidoAdminModel.obtenerNombreClientePorId(clienteId);
            UsuarioCache.agregarCliente(clienteId, nombreCliente);
        }
    }

    /**
     * Actualiza los elementos visuales de la interfaz para mostrar el estado de carga.
     *
     * @param mostrar Indica si se deben mostrar los indicadores de carga.
     * @param mensaje El mensaje que se mostrará durante la carga.
     */
    private void mostrarEstadoCarga(boolean mostrar, String mensaje) {
        Platform.runLater(() -> {
            if (progressBar != null) {
                progressBar.setVisible(mostrar);
                progressBar.setProgress(mostrar ? ProgressBar.INDETERMINATE_PROGRESS : 0);
            }

            if (statusLabel != null) {
                statusLabel.setVisible(mostrar);
                statusLabel.setText(mensaje);
            }

            // Desactivar los controles durante la carga
            filtroEstadoComboBox.setDisable(mostrar);
            btnRefrescar.setDisable(mostrar);
        });
    }

    /**
     * Muestra los detalles de un pedido seleccionado.
     * Carga asíncronamente la información detallada y actualiza el panel de detalles.
     *
     * @param pedido El pedido cuyos detalles se mostrarán.
     */
    private void mostrarDetallesPedido(Pedido pedido) {
        // Mostrar una indicación de carga
        mostrarEstadoCarga(true, "Cargando detalles del pedido...");

        Task<List<DetallePedido>> task = new Task<List<DetallePedido>>() {
            @Override
            protected List<DetallePedido> call() throws Exception {
                List<DetallePedido> detalles = PedidoModel.obtenerDetallesPedido(pedido.getId());

                // Cargar la información de productos para cada detalle
                for (DetallePedido detalle : detalles) {
                    int productoId = detalle.getProductoId();
                    Producto producto = ProductoCache.obtenerProducto(productoId);
                    if (producto == null) {
                        producto = ProductoModel.obtenerPorId(productoId);
                        if (producto == null) {
                            producto = new Producto();
                            producto.setNombre("Producto desconocido");
                        }
                        ProductoCache.agregarProducto(productoId, producto);
                    }
                    detalle.setProducto(producto);
                }

                return detalles;
            }
        };

        task.setOnSucceeded(event -> {
            List<DetallePedido> detalles = task.getValue();
            Platform.runLater(() -> {
                // Obtener el nombre del cliente (ya debería estar en caché)
                String nombreCliente = UsuarioCache.getNombreCliente(pedido.getClienteId());

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

                // Ocultar la indicación de carga
                mostrarEstadoCarga(false, "");
            });
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                mostrarAlerta("Error", "No se pudieron cargar los detalles",
                        task.getException().getMessage(), Alert.AlertType.ERROR);
                mostrarEstadoCarga(false, "");
            });
        });

        executorService.submit(task);
    }

    /**
     * Maneja el evento de clic en el botón de refrescar.
     * Reinicia la paginación y vuelve a cargar los pedidos iniciales.
     */
    @FXML
    private void onRefrescarClick() {
        resetearPaginacion();
        cargarPedidosIniciales();
    }

    /**
     * Maneja el evento de clic en el botón de cerrar detalles.
     * Oculta el panel de detalles del pedido y limpia la selección actual.
     */
    @FXML
    private void onCerrarDetallesClick() {
        // Ocultar el panel de detalles
        detallesPedidoPane.setVisible(false);
        pedidoSeleccionado = null;
    }


    /**
     * Maneja el evento de clic en el botón de cambiar estado.
     * Verifica que haya un pedido seleccionado antes de proceder.
     */
    @FXML
    private void onCambiarEstadoClick() {
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "No hay pedido seleccionado",
                    "Seleccione un pedido para cambiar su estado.", Alert.AlertType.ERROR);
            return;
        }
    }



    /**
     * Muestra una alerta con la información especificada.
     *
     * @param titulo El título de la alerta.
     * @param encabezado El texto de encabezado de la alerta.
     * @param contenido El contenido principal de la alerta.
     * @param tipo El tipo de alerta (información, advertencia, error, etc.).
     */
    private void mostrarAlerta(String titulo, String encabezado, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    /**
     * Maneja el evento de clic en el botón de asignar empleado.
     * Muestra un diálogo para seleccionar un empleado disponible y asignarlo al pedido seleccionado.
     */
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

    /**
     * Libera los recursos utilizados por el controlador.
     * Cierra el ExecutorService para detener las tareas asíncronas pendientes.
     * Este método debe llamarse al cerrar la ventana.
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}

