package com.example.cannagrow;

import com.example.model.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.List;

public class ProductosController {

    @FXML
    private FlowPane contenedorProductos;

    @FXML
    private Label tituloProductos;

    @FXML
    private HBox filtrosContainer;

    @FXML
    private TextField buscarTextField;

    private String filtroActual = "Todos";

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

        // Cargar todos los productos inicialmente
        cargarProductos();
    }

    private void configurarBusqueda() {
        buscarTextField.setOnAction(event -> {
            String termino = buscarTextField.getText().trim();
            if (termino.isEmpty()) {
                cargarProductos(); // Si está vacío, mostrar todos
            } else {
                buscarProductos(termino);
            }
        });
    }

    private void buscarProductos(String termino) {
        System.out.println("Buscando productos con: " + termino);

        // Limpiar el contenedor
        contenedorProductos.getChildren().clear();

        // Buscar productos por nombre
        List<Producto> productos = ProductoModel.buscarPorNombre(termino);

        if (productos.isEmpty()) {
            mostrarMensaje("No se encontraron productos con \"" + termino + "\"");
            return;
        }

        // Actualizar título
        tituloProductos.setText("Resultados para: \"" + termino + "\"");

        // Mostrar productos encontrados
        for (Producto p : productos) {
            VBox tarjeta = crearTarjetaProducto(p);
            contenedorProductos.getChildren().add(tarjeta);
        }
    }

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

            // Actualizar filtro actual
            filtroActual = tipo;

            // Cargar productos según el filtro
            if (tipo.equals("Todos")) {
                cargarProductos();
            } else {
                cargarProductosPorTipo(tipo);
            }
        });

        filtrosContainer.getChildren().add(filtroBtn);
    }

    private void cargarProductos() {
        System.out.println("Cargando todos los productos...");

        // Actualizar título si se cambió en una búsqueda
        if (tituloProductos != null) {
            tituloProductos.setText("Catálogo de Productos");
        }

        // Limpiar el contenedor primero
        contenedorProductos.getChildren().clear();

        List<Producto> productos = ProductoModel.obtenerTodos();
        System.out.println("Productos obtenidos: " + productos.size());

        if (productos.isEmpty()) {
            mostrarMensaje("No hay productos disponibles");
            return;
        }

        for (Producto p : productos) {
            VBox tarjeta = crearTarjetaProducto(p);
            contenedorProductos.getChildren().add(tarjeta);
        }
    }

    private void cargarProductosPorTipo(String tipo) {
        System.out.println("Cargando productos de tipo: " + tipo);

        // Actualizar título si existe
        if (tituloProductos != null) {
            tituloProductos.setText("Productos: " + tipo);
        }

        // Limpiar el contenedor primero
        contenedorProductos.getChildren().clear();

        // Obtener productos filtrados por tipo
        List<Producto> productos = ProductoModel.obtenerPorTipo(tipo);

        if (productos.isEmpty()) {
            mostrarMensaje("No hay productos de tipo " + tipo);
            return;
        }

        for (Producto p : productos) {
            VBox tarjeta = crearTarjetaProducto(p);
            contenedorProductos.getChildren().add(tarjeta);
        }
    }

    private void mostrarMensaje(String texto) {
        VBox mensajeBox = new VBox(20);
        mensajeBox.setAlignment(Pos.CENTER);
        mensajeBox.setPrefWidth(contenedorProductos.getPrefWidth());
        mensajeBox.setPrefHeight(300);

        // Icono (imagen triste o de información)
        try {
            InputStream iconStream = getClass().getResourceAsStream("/com/example/cannagrow/img/info-icon.png");
            if (iconStream != null) {
                ImageView icon = new ImageView(new Image(iconStream));
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

        // Cargar imagen con manejo de errores mejorado
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

        // Cargar icono para el botón de compra
        try {
            InputStream iconStream = getClass().getResourceAsStream("/com/example/cannagrow/img/cart.png");
            if (iconStream != null) {
                ImageView icon = new ImageView(new Image(iconStream, 16, 16, true, true));
                comprarBtn.setGraphic(icon);
            } else {
                comprarBtn.setText("+");
            }
        } catch (Exception e) {
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

    private boolean esProductoCannabis(String tipo) {
        // Estos tipos de productos normalmente tienen valores THC/CBD relevantes
        return tipo.equals("Flor") || tipo.equals("Aceite") ||
                tipo.equals("Comestible") || tipo.equals("Extracto");
    }

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

    private void cargarImagen(String url, ImageView imageView) {
        try {
            if (url != null && !url.isEmpty()) {
                // Primero intentamos cargar desde la URL proporcionada
                System.out.println("Intentando cargar imagen desde: " + url);

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
                        System.out.println("Intentando ruta alternativa: " + alt);
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

    private void cargarImagenPorDefecto(ImageView imageView) {
        try {
            InputStream is = getClass().getResourceAsStream("/com/example/cannagrow/img/sin_imagen.png");
            if (is != null) {
                imageView.setImage(new Image(is));
            } else {
                // Intentar con rutas alternativas
                String[] alternativas = {
                        "/img/sin_imagen.png",
                        "/images/sin_imagen.png",
                        "/sin_imagen.png"
                };

                for (String alt : alternativas) {
                    InputStream altIs = getClass().getResourceAsStream(alt);
                    if (altIs != null) {
                        imageView.setImage(new Image(altIs));
                        return;
                    }
                }

                // Si no se encuentra ninguna imagen, creamos una imagen vacía
                imageView.setStyle("-fx-background-color: #333333;");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen por defecto: " + e.getMessage());
            imageView.setStyle("-fx-background-color: #333333;");
        }
    }

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

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}