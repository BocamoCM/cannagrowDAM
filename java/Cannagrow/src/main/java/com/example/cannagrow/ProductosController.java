package com.example.cannagrow;

import com.example.model.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la visualización y gestión de productos en la interfaz gráfica.
 * Implementa funcionalidades de filtrado, búsqueda, lazy loading y caché de imágenes
 * para optimizar el rendimiento en la visualización del catálogo de productos.
 */
public class ProductosController {

    @FXML
    private FlowPane contenedorProductos;

    @FXML
    private Label tituloProductos;

    @FXML
    private HBox filtrosContainer;

    @FXML
    private TextField buscarTextField;

    @FXML
    private ScrollPane scrollPane;  // Asegúrate de tener un ScrollPane en tu FXML

    private String filtroActual = "Todos";
    private List<Producto> productosActuales = new ArrayList<>();
    private int elementosPorCarga = 90;  // Número de productos a cargar en cada carga
    private int totalProductosCargados = 0;
    private boolean cargandoProductos = false;
    private String terminoBusquedaActual = "";

    // Caché de imágenes
    private static final Map<String, Image> imagenCache = new HashMap<>();
    private static final Image DEFAULT_IMAGE; // Imagen por defecto

    static {
        // Inicializar la imagen por defecto una sola vez
        Image defaultImg = null;
        try {
            InputStream is = ProductosController.class.getResourceAsStream("/com/example/cannagrow/img/sin_imagen.png");
            if (is != null) {
                defaultImg = new Image(is);
                is.close();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen por defecto: " + e.getMessage());
        } finally {
            DEFAULT_IMAGE = defaultImg;
        }
    }

    /**
     * Inicializa la interfaz de usuario y configura los componentes necesarios.
     * Establece el título, configura los filtros, la búsqueda, el scroll listener
     * y carga los productos iniciales.
     */
    @FXML
    public void initialize() {
        System.out.println("Inicializando ProductosController...");

        // Verificar si los componentes de la interfaz se cargaron correctamente
        if (contenedorProductos == null) {
            System.err.println("ERROR: contenedorProductos es null");
            return;
        }

        // Configurar el título
        if (tituloProductos != null) {
            tituloProductos.setText("Catálogo de Productos");
        }

        // Inicializar filtros
        if (filtrosContainer != null) {
            configurarFiltros();
        }

        // Configurar búsqueda si existe el TextField
        if (buscarTextField != null) {
            configurarBusqueda();
        }

        // Configurar scroll listener para lazy loading
        if (scrollPane != null) {
            configurarScrollListener();
        }

        // Cargar la primera página de productos
        cargarProductos();
    }

    /**
     * Configura un listener para el ScrollPane que detecta cuando el usuario
     * se acerca al final del scroll para cargar más productos (lazy loading).
     */
    private void configurarScrollListener() {
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            // Si el usuario ha llegado cerca del final del scroll y no estamos ya cargando productos
            if (newValue.doubleValue() > 0.8 && !cargandoProductos &&
                    totalProductosCargados < productosActuales.size()) {
                cargarMasProductos();
            }
        });
    }

    /**
     * Configura el campo de búsqueda para filtrar productos por nombre.
     */
    private void configurarBusqueda() {
        buscarTextField.setOnAction(event -> {
            String termino = buscarTextField.getText().trim();
            terminoBusquedaActual = termino;

            if (termino.isEmpty()) {
                filtroActual = "Todos";
                cargarProductos(); // Si está vacío, mostrar todos
            } else {
                buscarProductos(termino);
            }
        });
    }

    /**
     * Configura el campo de búsqueda para filtrar productos por nombre.
     */
    private void buscarProductos(String termino) {
        System.out.println("Buscando productos con: " + termino);

        // Limpiar el contenedor
        contenedorProductos.getChildren().clear();
        totalProductosCargados = 0;

        // Buscar productos por nombre
        productosActuales = ProductoModel.buscarPorNombre(termino);

        if (productosActuales.isEmpty()) {
            mostrarMensaje("No se encontraron productos con \"" + termino + "\"");
            return;
        }

        // Actualizar título
        tituloProductos.setText("Resultados para: \"" + termino + "\"");

        // Mostrar los primeros productos
        cargarMasProductos();
    }

    /**
     * Configura los botones de filtro disponibles según los tipos de productos existentes.
     */
    private void configurarFiltros() {
        // Limpiar filtros existentes
        filtrosContainer.getChildren().clear();

        // Añadir etiqueta de filtros
        Label filtroLabel = new Label("Filtrar por: ");
        filtroLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 14px;");
        filtrosContainer.getChildren().add(filtroLabel);

        // Añadir botón "Todos"
        crearBotonFiltro("Todos");

        // Obtener tipos de productos de la base de datos
        List<String> tiposProductos = ProductoModel.obtenerTiposDisponibles();

        // Crear botones de filtro para cada tipo
        for (String tipo : tiposProductos) {
            crearBotonFiltro(tipo);
        }
    }

    /**
     * Crea un botón de filtro para un tipo específico de producto.
     *
     * @param tipo El tipo de producto para el filtro
     */
    private void crearBotonFiltro(String tipo) {
        Button filtroBtn = new Button(tipo);
        filtroBtn.getStyleClass().add("filtro-btn");

        // Si este botón corresponde al filtro actual, marcarlo como seleccionado
        if (tipo.equals(filtroActual)) {
            filtroBtn.getStyleClass().add("filtro-seleccionado");
            filtroBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
        }

        // Configurar acción al hacer clic
        filtroBtn.setOnAction(event -> {
            // Actualizar estilo de todos los botones
            for (var node : filtrosContainer.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    btn.getStyleClass().remove("filtro-seleccionado");
                    btn.setStyle("-fx-background-color: #1f1f1f; -fx-text-fill: #9e9e9e;");
                }
            }

            // Marcar este botón como seleccionado
            filtroBtn.getStyleClass().add("filtro-seleccionado");
            filtroBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");

            // Actualizar filtro actual y limpiar búsqueda
            filtroActual = tipo;
            terminoBusquedaActual = "";
            buscarTextField.setText("");

            // Cargar productos según el filtro
            if (tipo.equals("Todos")) {
                cargarProductos();
            } else {
                cargarProductosPorTipo(tipo);
            }
        });

        filtrosContainer.getChildren().add(filtroBtn);
    }

    /**
     * Carga todos los productos disponibles en la base de datos.
     * Limpia la vista actual y muestra los productos en el contenedor.
     */
    private void cargarProductos() {
        System.out.println("Cargando productos iniciales...");

        // Actualizar título si se cambió en una búsqueda
        if (tituloProductos != null) {
            tituloProductos.setText("Catálogo de Productos");
        }

        // Limpiar el contenedor primero
        contenedorProductos.getChildren().clear();
        totalProductosCargados = 0;

        // Obtener todos los productos disponibles (solo IDs y datos básicos)
        productosActuales = ProductoModel.obtenerTodos();
        System.out.println("Total de productos disponibles: " + productosActuales.size());

        if (productosActuales.isEmpty()) {
            mostrarMensaje("No hay productos disponibles");
            return;
        }

        // Cargar la primera página de productos
        cargarMasProductos();
    }

    /**
     * Carga los productos filtrados por un tipo específico.
     *
     * @param tipo El tipo de producto para filtrar
     */
    private void cargarProductosPorTipo(String tipo) {
        System.out.println("Cargando productos de tipo: " + tipo);

        // Actualizar título si existe
        if (tituloProductos != null) {
            tituloProductos.setText("Productos: " + tipo);
        }

        // Limpiar el contenedor primero
        contenedorProductos.getChildren().clear();
        totalProductosCargados = 0;

        // Obtener productos filtrados por tipo
        productosActuales = ProductoModel.obtenerPorTipo(tipo);

        if (productosActuales.isEmpty()) {
            mostrarMensaje("No hay productos de tipo " + tipo);
            return;
        }

        // Cargar la primera página de productos
        cargarMasProductos();
    }

    /**
     * Implementa la funcionalidad de lazy loading para cargar más productos
     * a medida que el usuario hace scroll en la interfaz.
     */
    private void cargarMasProductos() {
        if (cargandoProductos || totalProductosCargados >= productosActuales.size()) {
            return; // Ya estamos cargando o ya cargamos todo
        }

        cargandoProductos = true;

        // Determinar cuántos productos más cargar
        int fin = Math.min(totalProductosCargados + elementosPorCarga, productosActuales.size());
        System.out.println("Cargando productos del " + totalProductosCargados + " al " + (fin-1));

        // Cargar los siguientes productos
        for (int i = totalProductosCargados; i < fin; i++) {
            Producto p = productosActuales.get(i);
            VBox tarjeta = crearTarjetaProducto(p);
            contenedorProductos.getChildren().add(tarjeta);
        }

        // Actualizar contador
        totalProductosCargados = fin;
        cargandoProductos = false;
    }

    /**
     * Muestra un mensaje informativo cuando no hay productos disponibles
     * según los filtros o términos de búsqueda actuales.
     *
     * @param texto El mensaje a mostrar
     */
    private void mostrarMensaje(String texto) {
        VBox mensajeBox = new VBox(20);
        mensajeBox.setAlignment(Pos.CENTER);
        mensajeBox.setPrefWidth(contenedorProductos.getPrefWidth());
        mensajeBox.setPrefHeight(300);

        // Icono (imagen triste o de información)
        try {
            Image infoIcon = getImagenCache("/com/example/cannagrow/img/info-icon.png");
            if (infoIcon != null) {
                ImageView icon = new ImageView(infoIcon);
                icon.setFitHeight(64);
                icon.setFitWidth(64);
                mensajeBox.getChildren().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar icono: " + e.getMessage());
        }

        // Mensaje
        Label mensaje = new Label(texto);
        mensaje.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 18px;");

        // Sugerencia
        Label sugerencia = new Label("Intenta cambiar los filtros o realizar otra búsqueda");
        sugerencia.setStyle("-fx-text-fill: #757575; -fx-font-size: 14px;");

        mensajeBox.getChildren().addAll(mensaje, sugerencia);
        contenedorProductos.getChildren().add(mensajeBox);
    }

    /**
     * Crea una tarjeta visual para un producto con todos sus detalles.
     *
     * @param producto El producto para el cual crear la tarjeta
     * @return Un contenedor VBox con la información y controles del producto
     */
    private VBox crearTarjetaProducto(Producto producto) {
        // Contenedor principal
        VBox tarjeta = new VBox(8);
        tarjeta.getStyleClass().add("tarjeta-producto");
        tarjeta.setPrefWidth(200);
        tarjeta.setPrefHeight(300);
        tarjeta.setAlignment(Pos.TOP_CENTER);

        // Crear efecto de hover
        tarjeta.setOnMouseEntered(e -> {
            tarjeta.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 12; -fx-padding: 12;");
            tarjeta.setEffect(new javafx.scene.effect.DropShadow(10, Color.rgb(0, 150, 0, 0.5)));
        });

        tarjeta.setOnMouseExited(e -> {
            tarjeta.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 12; -fx-padding: 12;");
            tarjeta.setEffect(null);
        });

        // Configurar estilo inicial
        tarjeta.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 12; -fx-padding: 12;");

        // Contenedor de imagen con borde redondeado
        StackPane imagenContainer = new StackPane();
        imagenContainer.setStyle("-fx-background-color: #2c2c2c; -fx-background-radius: 8;");
        imagenContainer.setPrefHeight(150);
        imagenContainer.setMinHeight(150);

        // Imagen del producto
        ImageView imagen = new ImageView();
        imagen.setFitWidth(160);
        imagen.setFitHeight(120);
        imagen.setPreserveRatio(true);

        // Cargar imagen con manejo de errores mejorado usando caché
        cargarImagen(producto.getImagenProducto(), imagen);

        imagenContainer.getChildren().add(imagen);

        // Etiqueta para el tipo de producto
        Label tipoLabel = crearEtiquetaTipo(producto.getTipo());

        // Nombre del producto
        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.setWrapText(true);
        nombreLabel.setMaxWidth(180);
        nombreLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Información adicional (THC/CBD)
        HBox infoContainer = new HBox(10);
        infoContainer.setAlignment(Pos.CENTER);

        // Solo mostrar THC/CBD si son relevantes para el tipo de producto
        if (esProductoCannabis(producto.getTipo())) {
            Label thcLabel = new Label("THC: " + String.format("%.1f%%", producto.getContenidoTHC()));
            thcLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");

            Label cbdLabel = new Label("CBD: " + String.format("%.1f%%", producto.getContenidoCBD()));
            cbdLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");

            infoContainer.getChildren().addAll(thcLabel, cbdLabel);
        } else {
            // Si no es un producto de cannabis, mostrar el tipo de producto
            Label categoriaLabel = new Label(producto.getTipo());
            categoriaLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");
            infoContainer.getChildren().add(categoriaLabel);
        }

        // Separador
        Separator separador = new Separator();
        separador.setOpacity(0.3);

        // Precio y botón
        HBox precioContainer = new HBox();
        precioContainer.setAlignment(Pos.CENTER_LEFT);
        precioContainer.setSpacing(10);

        Label precioLabel = new Label(String.format("€%.2f", producto.getPrecio()));
        precioLabel.setStyle("-fx-text-fill: #7cb342; -fx-font-weight: bold; -fx-font-size: 16px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button comprarBtn = new Button("");
        comprarBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-background-radius: 50%;");
        comprarBtn.setPrefSize(30, 30);

        // Cargar icono para el botón de compra con caché
        Image cartIcon = getImagenCache("/com/example/cannagrow/img/cart.png");
        if (cartIcon != null) {
            ImageView icon = new ImageView(cartIcon);
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            comprarBtn.setGraphic(icon);
        } else {
            comprarBtn.setText("+");
        }

        // Manejar clic en el botón
        comprarBtn.setOnAction(e -> agregarAlCarrito(producto));

        precioContainer.getChildren().addAll(precioLabel, spacer, comprarBtn);

        // Estado de stock
        Label stockLabel = crearEtiquetaStock(producto.getStock());

        // Agregar todos los elementos a la tarjeta
        tarjeta.getChildren().addAll(
                imagenContainer,
                tipoLabel,
                nombreLabel,
                infoContainer,
                separador,
                precioContainer,
                stockLabel
        );

        return tarjeta;
    }

    /**
     * Determina si un producto es de cannabis basado en su tipo.
     *
     * @param tipo El tipo de producto
     * @return true si es un producto de cannabis, false en caso contrario
     */
    private boolean esProductoCannabis(String tipo) {
        // Estos tipos de productos normalmente tienen valores THC/CBD relevantes
        return tipo.equals("Flor") || tipo.equals("Aceite") ||
                tipo.equals("Comestible") || tipo.equals("Extracto");
    }

    /**
     * Crea una etiqueta visual para mostrar el tipo de producto.
     *
     * @param tipo El tipo de producto
     * @return Una etiqueta estilizada según el tipo
     */
    private Label crearEtiquetaTipo(String tipo) {
        Label tipoLabel = new Label(tipo);
        tipoLabel.getStyleClass().add("category-label");

        // Asignar un color según el tipo
        switch (tipo) {
            case "Flor":
                tipoLabel.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
            case "Aceite":
                tipoLabel.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
            case "Comestible":
                tipoLabel.setStyle("-fx-background-color: #e64a19; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
            case "Extracto":
                tipoLabel.setStyle("-fx-background-color: #5e35b1; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
            case "Semilla":
                tipoLabel.setStyle("-fx-background-color: #f57f17; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
            case "Cosmético":
                tipoLabel.setStyle("-fx-background-color: #d81b60; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
            default:
                tipoLabel.setStyle("-fx-background-color: #546e7a; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 12;");
                break;
        }

        return tipoLabel;
    }

    /**
     * Crea una etiqueta que muestra el estado del stock del producto.
     *
     * @param stock La cantidad disponible del producto
     * @return Una etiqueta estilizada según el nivel de stock
     */
    private Label crearEtiquetaStock(int stock) {
        Label stockLabel = new Label();

        if (stock > 10) {
            stockLabel.setText("En stock");
            stockLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-size: 11px;");
        } else if (stock > 0) {
            stockLabel.setText("¡Últimas unidades! (" + stock + ")");
            stockLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-size: 11px;");
        } else {
            stockLabel.setText("Agotado");
            stockLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 11px;");
        }

        return stockLabel;
    }

    /**
     * Obtiene una imagen de la caché o la carga y almacena si no existe.
     *
     * @param ruta La ruta de la imagen
     * @return La imagen cargada o una imagen por defecto si no se pudo cargar
     */
    private Image getImagenCache(String ruta) {
        // Si la ruta es nula o vacía, devolver la imagen por defecto
        if (ruta == null || ruta.isEmpty()) {
            return DEFAULT_IMAGE;
        }

        // Comprobar si la imagen ya está en caché
        if (imagenCache.containsKey(ruta)) {
            return imagenCache.get(ruta);
        }

        // Cargar la imagen e incorporar a caché
        try {
            Image imagen = null;

            // Si es una ruta de recurso
            if (ruta.startsWith("/")) {
                InputStream is = getClass().getResourceAsStream(ruta);
                if (is != null) {
                    imagen = new Image(is);
                    is.close();
                } else {
                    // Intentar con rutas alternativas
                    String[] alternativas = {
                            "/com/example/cannagrow" + ruta,
                            "/img" + ruta.substring(ruta.lastIndexOf('/')),
                            "/images" + ruta.substring(ruta.lastIndexOf('/'))
                    };

                    for (String alt : alternativas) {
                        InputStream altIs = getClass().getResourceAsStream(alt);
                        if (altIs != null) {
                            imagen = new Image(altIs);
                            altIs.close();
                            break;
                        }
                    }
                }
            } else {
                // Si es una URL externa o una ruta del sistema de archivos
                imagen = new Image(ruta, true); // El segundo parámetro indica carga en background
            }

            if (imagen != null && !imagen.isError()) {
                imagenCache.put(ruta, imagen);
                return imagen;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen para caché: " + e.getMessage());
        }

        // Si no se pudo cargar, usar la imagen por defecto y cachearla para esta ruta
        imagenCache.put(ruta, DEFAULT_IMAGE);
        return DEFAULT_IMAGE;
    }

    /**
     * Carga una imagen en un ImageView utilizando la caché.
     *
     * @param url La URL o ruta de la imagen
     * @param imageView El componente ImageView donde mostrar la imagen
     */
    private void cargarImagen(String url, ImageView imageView) {
        Image imagen = getImagenCache(url);
        imageView.setImage(imagen);
    }

    /**
     * Añade un producto al carrito del usuario actual.
     * Verifica la sesión y el stock antes de agregar.
     *
     * @param producto El producto a añadir al carrito
     */

    private void agregarAlCarrito(Producto producto) {
        // Verificar si hay un usuario en sesión
        UsuarioModel usuario = Session.getUsuarioActual();

        if (usuario == null) {
            mostrarAlerta("Iniciar sesión", "Por favor, inicia sesión para añadir productos al carrito.", Alert.AlertType.INFORMATION);
            return;
        }

        // Verificar stock
        if (producto.getStock() <= 0) {
            mostrarAlerta("Producto agotado", "Lo sentimos, este producto está agotado.", Alert.AlertType.WARNING);
            return;
        }

        // Añadir el producto al carrito usando el CarritoModel
        boolean agregado = CarritoModel.agregarProducto(producto, 1);

        if (agregado) {
            mostrarAlerta("Producto añadido",
                    "Se ha añadido '" + producto.getNombre() + "' al carrito.",
                    Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error",
                    "No se pudo añadir el producto al carrito. Intente nuevamente.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra una alerta con la información proporcionada.
     *
     * @param titulo El título de la alerta
     * @param mensaje El mensaje a mostrar
     * @param tipo El tipo de alerta (información, advertencia, error)
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Recarga los productos en la interfaz según el filtro actual.
     * Útil después de realizar cambios en los datos.
     */
    public void recargarProductos() {
        terminoBusquedaActual = "";
        buscarTextField.setText("");

        if (filtroActual.equals("Todos")) {
            cargarProductos();
        } else {
            cargarProductosPorTipo(filtroActual);
        }
    }

    /**
     * Limpia la caché de imágenes para liberar memoria.
     * Útil cuando se cambia de pantalla o se cierra la aplicación.
     */
    public static void limpiarCache() {
        imagenCache.clear();
        System.out.println("Caché de imágenes limpiada");
    }
}