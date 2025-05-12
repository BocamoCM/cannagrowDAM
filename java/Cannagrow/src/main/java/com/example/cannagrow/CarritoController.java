package com.example.cannagrow;

import com.example.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class CarritoController {

    @FXML
    private VBox carritoVacioView;

    @FXML
    private VBox carritoContenidoView;

    @FXML
    private VBox itemsContainer;

    @FXML
    private ImageView carritoVacioIcon;

    @FXML
    private Button vaciarCarritoBtn;

    @FXML
    private Button irTiendaBtn;

    @FXML
    private Button finalizarCompraBtn;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label envioLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private TextField direccionField;

    @FXML
    private TextField codigoPostalField;

    @FXML
    private TextField ciudadField;

    @FXML
    private TextField telefonoField;

    @FXML
    private BorderPane rootPane;

    // Constante para gastos de envío
    private final float GASTOS_ENVIO = 4.99f;

    // Formato para mostrar precios
    private final DecimalFormat formatoPrecio = new DecimalFormat("€#,##0.00");

    @FXML
    public void initialize() {
        System.out.println("Inicializando CarritoController...");

        // Cargar el icono de carrito vacío
        cargarIconoCarritoVacio();

        // Verificar si hay un usuario en sesión
        UsuarioModel usuario = Session.getUsuarioActual();
        if (usuario == null) {
            mostrarAlerta("Iniciar sesión",
                    "Por favor, inicia sesión para acceder al carrito.",
                    Alert.AlertType.INFORMATION);

            // Redirigir a la página de inicio de sesión
            irALogin();
            return;
        }

        // Cargar el contenido del carrito
        cargarCarrito();

        // Prellenar información del usuario si está disponible
        prellenarInformacionUsuario(usuario);
    }

    /**
     * Carga el icono de carrito vacío
     */
    private void cargarIconoCarritoVacio() {
        try {
            // Intentar múltiples rutas para encontrar la imagen
            String[] rutasCarrito = {
                    "/com/example/cannagrow/img/cart.png",
                    "/img/cart.png",
                    "/images/cart.png",
                    "/cart.png"
            };

            boolean imagenCargada = false;

            for (String ruta : rutasCarrito) {
                InputStream iconStream = getClass().getResourceAsStream(ruta);
                if (iconStream != null) {
                    carritoVacioIcon.setImage(new Image(iconStream));
                    System.out.println("Imagen de carrito vacío cargada desde: " + ruta);
                    imagenCargada = true;
                    break;
                }
            }

            if (!imagenCargada) {
                System.err.println("No se pudo cargar el icono del carrito vacío");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el icono del carrito: " + e.getMessage());
        }
    }

    /**
     * Prellenar información del usuario en los campos de envío
     */
    private void prellenarInformacionUsuario(UsuarioModel usuario) {
        // Si en el futuro se implementa guardar direcciones de envío, aquí se cargarían
        // Por ahora se dejan los campos vacíos para que el usuario los llene
    }

    /**
     * Carga los productos del carrito
     */
    private void cargarCarrito() {
        // Limpiar el contenedor de items
        itemsContainer.getChildren().clear();

        // Obtener los items del carrito
        List<CarritoModel.ItemCarrito> items = CarritoModel.getItems();

        // Mostrar la vista apropiada según si el carrito está vacío o no
        if (items.isEmpty()) {
            carritoVacioView.setVisible(true);
            carritoContenidoView.setVisible(false);
            vaciarCarritoBtn.setVisible(false);
        } else {
            carritoVacioView.setVisible(false);
            carritoContenidoView.setVisible(true);
            vaciarCarritoBtn.setVisible(true);

            // Mostrar cada producto en el carrito
            for (CarritoModel.ItemCarrito item : items) {
                HBox itemRow = crearFilaItem(item);
                itemsContainer.getChildren().add(itemRow);
            }

            // Actualizar resumen del pedido
            actualizarResumen();
        }
    }

    /**
     * Crea una fila para un item del carrito
     */
    private HBox crearFilaItem(CarritoModel.ItemCarrito item) {
        Producto producto = item.getProducto();

        // Contenedor principal de la fila
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 5;");

        // Imagen del producto
        ImageView imagenView = new ImageView();
        imagenView.setFitWidth(80);
        imagenView.setFitHeight(80);
        imagenView.setPreserveRatio(true);

        // Cargar imagen
        cargarImagen(producto.getImagenProducto(), imagenView);

        // Contenedor para la información del producto
        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        // Nombre del producto
        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Tipo/categoría del producto
        Label tipoLabel = new Label(producto.getTipo());
        tipoLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");

        // Precio unitario
        Label precioLabel = new Label(formatoPrecio.format(producto.getPrecio()));
        precioLabel.setStyle("-fx-text-fill: #7cb342; -fx-font-size: 14px;");

        infoContainer.getChildren().addAll(nombreLabel, tipoLabel, precioLabel);

        // Spinner para la cantidad
        Spinner<Integer> cantidadSpinner = new Spinner<>(1, producto.getStock(), item.getCantidad());
        cantidadSpinner.setEditable(true);
        cantidadSpinner.setPrefWidth(80);
        cantidadSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Actualizar la cantidad en el carrito
            if (CarritoModel.actualizarCantidad(producto, newValue)) {
                item.setCantidad(newValue);
                actualizarResumen();

                // Actualizar el subtotal en esta fila
                subtotalLabel.setText(formatoPrecio.format(item.getSubtotal()));
            } else {
                // Si no hay suficiente stock, volver al valor anterior
                cantidadSpinner.getValueFactory().setValue(oldValue);
                mostrarAlerta("Stock insuficiente",
                        "No hay suficiente stock disponible para " + producto.getNombre(),
                        Alert.AlertType.WARNING);
            }
        });

        // Subtotal para este item
        Label itemSubtotalLabel = new Label(formatoPrecio.format(item.getSubtotal()));
        itemSubtotalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        itemSubtotalLabel.setPrefWidth(80);
        itemSubtotalLabel.setAlignment(Pos.CENTER_RIGHT);

        // Botón eliminar
        Button eliminarBtn = new Button("");
        eliminarBtn.setStyle("-fx-background-color: #e53935; -fx-background-radius: 50%;");
        eliminarBtn.setPrefSize(30, 30);

        // Cargar icono para el botón eliminar
        try {
            String[] rutasBorrar = {
                    "/com/example/cannagrow/img/delete.png",
                    "/img/delete.png",
                    "/images/delete.png",
                    "/delete.png"
            };

            boolean iconoCargado = false;

            for (String ruta : rutasBorrar) {
                InputStream iconStream = getClass().getResourceAsStream(ruta);
                if (iconStream != null) {
                    ImageView icon = new ImageView(new Image(iconStream, 16, 16, true, true));
                    eliminarBtn.setGraphic(icon);
                    iconoCargado = true;
                    break;
                }
            }

            if (!iconoCargado) {
                eliminarBtn.setText("X");
            }
        } catch (Exception e) {
            eliminarBtn.setText("X");
        }

        // Manejar clic en el botón eliminar
        eliminarBtn.setOnAction(e -> {
            CarritoModel.eliminarProducto(producto);
            cargarCarrito(); // Recargar todo el carrito
        });

        // Agregar todos los elementos a la fila
        row.getChildren().addAll(imagenView, infoContainer, cantidadSpinner, itemSubtotalLabel, eliminarBtn);

        return row;
    }

    /**
     * Cargar imagen del producto
     */
    private void cargarImagen(String url, ImageView imageView) {
        try {
            if (url != null && !url.isEmpty()) {
                // Si es una ruta de recurso
                if (url.startsWith("/")) {
                    InputStream is = getClass().getResourceAsStream(url);
                    if (is != null) {
                        imageView.setImage(new Image(is));
                        return;
                    }

                    // Intentar con rutas alternativas
                    String[] alternativas = {
                            "/com/example/cannagrow" + url,
                            "/img" + url.substring(url.lastIndexOf('/')),
                            "/images" + url.substring(url.lastIndexOf('/'))
                    };

                    for (String alt : alternativas) {
                        InputStream altIs = getClass().getResourceAsStream(alt);
                        if (altIs != null) {
                            imageView.setImage(new Image(altIs));
                            return;
                        }
                    }
                } else {
                    // Si es una URL externa o una ruta del sistema de archivos
                    imageView.setImage(new Image(url, true));
                    return;
                }
            }

            // Si llegamos aquí, cargar la imagen por defecto
            cargarImagenPorDefecto(imageView);

        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            cargarImagenPorDefecto(imageView);
        }
    }

    /**
     * Cargar imagen por defecto
     */
    private void cargarImagenPorDefecto(ImageView imageView) {
        try {
            String[] rutasDefault = {
                    "/com/example/cannagrow/img/sin_imagen.png",
                    "/img/sin_imagen.png",
                    "/images/sin_imagen.png",
                    "/sin_imagen.png"
            };

            boolean defaultCargado = false;

            for (String ruta : rutasDefault) {
                InputStream is = getClass().getResourceAsStream(ruta);
                if (is != null) {
                    imageView.setImage(new Image(is));
                    defaultCargado = true;
                    break;
                }
            }

            if (!defaultCargado) {
                imageView.setStyle("-fx-background-color: #333333;");
            }
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: #333333;");
        }
    }

    /**
     * Actualiza el resumen del pedido
     */
    private void actualizarResumen() {
        float subtotal = CarritoModel.getTotal();
        float total = subtotal + GASTOS_ENVIO;

        subtotalLabel.setText(formatoPrecio.format(subtotal));
        envioLabel.setText(formatoPrecio.format(GASTOS_ENVIO));
        totalLabel.setText(formatoPrecio.format(total));
    }

    /**
     * Vaciar el carrito
     */
    @FXML
    private void vaciarCarrito() {
        // Mostrar confirmación antes de vaciar el carrito
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Vaciar Carrito");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que quieres vaciar todo el carrito?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CarritoModel.vaciarCarrito();
            cargarCarrito();
        }
    }

    /**
     * Ir a la página de productos manteniendo el menú
     */
    @FXML
    private void irATienda() {
        try {
            // Intentamos obtener el BorderPane raíz que contiene el menú
            Scene currentScene = carritoVacioView.getScene();
            if (currentScene == null) {
                System.err.println("Error: No se puede obtener la escena actual");
                return;
            }

            BorderPane menuRoot = null;
            for (Node node : currentScene.getRoot().lookupAll(".root")) {
                if (node instanceof BorderPane && ((BorderPane) node).getLeft() != null) {
                    menuRoot = (BorderPane) node;
                    break;
                }
            }

            if (menuRoot != null) {
                // Si encontramos el BorderPane con el menú, cargamos los productos en su centro
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/Productos.fxml"));
                    AnchorPane productosPane = loader.load();
                    menuRoot.setCenter(productosPane);
                    System.out.println("Productos cargados en el centro del menú principal");
                } catch (IOException e) {
                    System.err.println("Error al cargar la vista de productos: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Si no encontramos el BorderPane, intentamos invocar el método del MenuController
                Stage stage = (Stage) carritoVacioView.getScene().getWindow();
                SceneChanger.changeScene("/com/example/cannagrow/inicio.fxml", stage);
            }
        } catch (Exception e) {
            System.err.println("Error al navegar a la tienda: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de productos.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Ir a la página de inicio de sesión
     */
    private void irALogin() {
        try {
            Stage stage = (Stage) carritoVacioView.getScene().getWindow();
            SceneChanger.changeScene("/com/example/cannagrow/hello-view.fxml", stage);
        } catch (Exception e) {
            System.err.println("Error al cargar la vista de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Finalizar la compra
     */
    @FXML
    private void finalizarCompra() {
        System.out.println("DEBUG: Iniciando proceso de finalización de compra...");

        UsuarioModel usuario = Session.getUsuarioActual(); // Cambiar si usas ClienteModel

        if (usuario == null) {
            System.out.println("ERROR: No hay usuario en sesión.");
            mostrarAlerta("Error", "No hay usuario en sesión.", Alert.AlertType.ERROR);
            return;
        }

        int clienteId = usuario.getId();
        System.out.println("DEBUG: Usuario en sesión -> ID: " + clienteId);

        // Verifica si el cliente realmente existe en la base de datos (esto es importante)
        if (!clienteExisteEnBD(clienteId)) {
            System.out.println("ERROR: El cliente con ID " + clienteId + " no existe en la base de datos.");
            mostrarAlerta("Error", "Cliente no encontrado en la base de datos.", Alert.AlertType.ERROR);
            return;
        }

        List<String> matriculas = PedidoModel.obtenerMatriculasVehiculos();
        System.out.println("DEBUG: Vehículos disponibles: " + matriculas);

        String matricula = matriculas.isEmpty() ? "DEFAULT01" : matriculas.get(0);
        System.out.println("DEBUG: Matrícula seleccionada: " + matricula);

        System.out.println("DEBUG: Intentando crear pedido con cliente_id = " + clienteId);

        boolean exito = CarritoModel.crearPedidoDesdeCarrito(clienteId, matricula);

        if (exito) {
            System.out.println("DEBUG: Pedido creado exitosamente.");
            mostrarAlerta("Pedido realizado",
                    "¡Tu pedido ha sido procesado con éxito!\nRecibirás un email con los detalles.",
                    Alert.AlertType.INFORMATION);
            irATienda();
        } else {
            System.out.println("ERROR: No se pudo crear el pedido.");
            mostrarAlerta("Error", "No se pudo procesar el pedido. Intenta de nuevo.", Alert.AlertType.ERROR);
        }
    }

    private boolean clienteExisteEnBD(int clienteId) {
        String sql = "SELECT id FROM Cliente WHERE id = ?";
        try (Connection conn = DBUtil.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar cliente en BD: " + e.getMessage());
            return false;
        }
    }




    /**
     * Muestra una alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}