package com.example.cannagrow;

import com.example.model.Categoria;
import com.example.model.CarritoModel;
import com.example.model.Session;
import com.example.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MenuController {
    @FXML
    private Button carritoButton;
    @FXML
    private Button pedidosButton;
    @FXML
    private Button productosButton;
    @FXML
    private Button inicioButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ImageView logoImage;
    @FXML
    private Button adminButton;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private FlowPane contenedorCategorias; // Este es el elemento que estaba causando el NullPointerException
    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        System.out.println("Iniciando inicialización de MenuController...");

        // Primero verificamos si los elementos FXML están correctamente inyectados
        if (logoImage == null) {
            System.err.println("ERROR: logoImage es null");
        }

        if (contenedorCategorias == null) {
            System.err.println("ERROR: contenedorCategorias es null");
        } else {
            System.out.println("contenedorCategorias inicializado correctamente");
        }

        UsuarioModel usuario = Session.getUsuarioActual();

        // Cargar logo primero
        cargarLogo();

        // Luego cargar categorías si el contenedor existe
        if (contenedorCategorias != null) {
            cargarCategorias();
        }

        // Actualizar contador del carrito
        actualizarContadorCarrito();

        if (usuario != null) {
            // Si tiene rol es Empleado, si no es Cliente
            String tipoUsuario = (usuario.getRol() != null) ? capitalizar(usuario.getRol()) : "Cliente";
            System.out.println("Usuario actual: " + tipoUsuario);

            if (usuario.getRol() == null || usuario.getRol().equalsIgnoreCase("cliente")) {
                // CLIENTE
                carritoButton.setVisible(true);
                pedidosButton.setVisible(true);
                productosButton.setVisible(true);
                adminButton.setVisible(false);
            } else {
                // EMPLEADO
                String rol = usuario.getRol().toLowerCase();

                switch (rol) {
                    case "gerente":
                        carritoButton.setVisible(true);
                        pedidosButton.setVisible(true);
                        productosButton.setVisible(true);
                        adminButton.setVisible(true);
                        break;
                    case "vendedor":
                        carritoButton.setVisible(false);
                        pedidosButton.setVisible(true);
                        productosButton.setVisible(true);
                        adminButton.setVisible(false);
                        break;
                    default:
                        // Si el rol no está bien definido, mejor mostrarlo todo excepto admin
                        carritoButton.setVisible(true);
                        pedidosButton.setVisible(false);
                        productosButton.setVisible(true);
                        adminButton.setVisible(false);
                        break;
                }
            }
        } else {
            System.out.println("No hay usuario logueado");
        }

        System.out.println("Inicialización de MenuController completada");
    }

    /**
     * Actualiza el texto del botón del carrito para mostrar la cantidad de items
     */
    private void actualizarContadorCarrito() {
        try {
            if (carritoButton != null) {
                int cantidadItems = CarritoModel.getCantidadTotal();
                if (cantidadItems > 0) {
                    carritoButton.setText("Carrito (" + cantidadItems + ")");
                } else {
                    carritoButton.setText("Carrito");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar contador del carrito: " + e.getMessage());
        }
    }

    private void cargarLogo() {
        try {
            // Registrar la ruta que estamos intentando cargar
            String logoPath = "/com/example/cannagrow/cannagrow_logo.png";
            System.out.println("Intentando cargar logo desde: " + logoPath);

            InputStream logoStream = getClass().getResourceAsStream(logoPath);
            if (logoStream != null) {
                logoImage.setImage(new Image(logoStream));
                System.out.println("Logo cargado correctamente");
            } else {
                System.err.println("No se pudo encontrar el recurso del logo en: " + logoPath);

                // Intentar con rutas alternativas
                String[] rutasAlternativas = {
                        "/cannagrow_logo.png",
                        "/img/cannagrow_logo.png",
                        "/images/cannagrow_logo.png"
                };

                for (String ruta : rutasAlternativas) {
                    System.out.println("Intentando con ruta alternativa: " + ruta);
                    InputStream altStream = getClass().getResourceAsStream(ruta);
                    if (altStream != null) {
                        logoImage.setImage(new Image(altStream));
                        System.out.println("Logo cargado desde ruta alternativa: " + ruta);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para capitalizar el rol (ej. "gerente" → "Gerente")
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    private void cargarCategorias() {
        System.out.println("Iniciando carga de categorías...");

        // Limpiamos el contenedor primero para evitar duplicados
        contenedorCategorias.getChildren().clear();

        // Definimos categorías con sus imágenes
        Categoria[] categorias = {
                new Categoria("Fertilizantes", "/com/example/cannagrow/img/fertilizantes.png"),
                new Categoria("CBD", "/com/example/cannagrow/img/cbd.png"),
                new Categoria("Crecimiento", "/com/example/cannagrow/img/armarios.png")
        };

        // Contador para verificar cuántas categorías se cargaron correctamente
        int categoriasExitosas = 0;

        for (Categoria cat : categorias) {
            try {
                System.out.println("Cargando categoría: " + cat.getNombre() + " con imagen: " + cat.getImageUrl());

                // Verificamos primero si la imagen existe
                InputStream testStream = getClass().getResourceAsStream(cat.getImageUrl());
                if (testStream == null) {
                    System.err.println("ADVERTENCIA: Imagen no encontrada en: " + cat.getImageUrl());

                    // Intentar con rutas alternativas
                    String nombreArchivo = cat.getImageUrl().substring(cat.getImageUrl().lastIndexOf('/') + 1);
                    String[] rutasAlternativas = {
                            "/img/" + nombreArchivo,
                            "/images/" + nombreArchivo,
                            "/" + nombreArchivo
                    };

                    for (String ruta : rutasAlternativas) {
                        System.out.println("Intentando con ruta alternativa: " + ruta);
                        if (getClass().getResourceAsStream(ruta) != null) {
                            cat.setImageUrl(ruta);
                            System.out.println("Imagen encontrada en ruta alternativa: " + ruta);
                            break;
                        }
                    }
                } else {
                    testStream.close(); // No olvidar cerrar el stream
                }

                // Cargar el componente FXML para la categoría
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/CategoriaItem.fxml"));
                Parent item = loader.load();

                CategoriaItemController controller = loader.getController();
                controller.setCategoria(cat);

                contenedorCategorias.getChildren().add(item);
                categoriasExitosas++;
                System.out.println("Categoría " + cat.getNombre() + " agregada al contenedor");

            } catch (IOException e) {
                System.err.println("Error cargando categoría " + cat.getNombre() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Total de categorías cargadas exitosamente: " + categoriasExitosas + " de " + categorias.length);
    }

    @FXML
    private void onMostrarRegistroClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/register-view.fxml"));
            Parent registroVista = loader.load();
            mainBorderPane.setCenter(registroVista);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de registro.");
        }
    }

    @FXML
    private void onInicioClick(javafx.event.ActionEvent event) {
        try {
            if (rootPane != null) {
                resetearBotonesMenu();
                inicioButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
                if (contenedorCategorias != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/inicio.fxml"));
                    Parent inicioVista = loader.load();
                    return;
                }
            }
            // Si el rootPane es null o no se pudo cargar en el centro, cambiar toda la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneChanger.changeScene("/com/example/cannagrow/inicio.fxml", stage);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de inicio.");
        }
    }

    /**
     * Resetea los estilos de todos los botones del menú
     */
    private void resetearBotonesMenu() {
        String estiloNormal = "-fx-background-color: #555555; -fx-text-fill: white;";
        inicioButton.setStyle(estiloNormal);
        productosButton.setStyle(estiloNormal);
        carritoButton.setStyle(estiloNormal);
        pedidosButton.setStyle(estiloNormal);
    }

    @FXML
    public void onProductosClick() {
        try {
            resetearBotonesMenu();
            productosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            AnchorPane productosPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/Productos.fxml")));
            mainBorderPane.setCenter(productosPane);  // Usar mainBorderPane en lugar de rootPane
            actualizarContadorCarrito();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de productos.");
        }
    }

    @FXML
    private void onCarritoClick() {
        try {
            // Verificar si hay un usuario en sesión
            UsuarioModel usuario = Session.getUsuarioActual();
            if (usuario == null) {
                mostrarMensaje("Iniciar sesión",
                        "Por favor, inicia sesión para acceder al carrito.",
                        Alert.AlertType.INFORMATION);
                return;
            }

            resetearBotonesMenu();
            carritoButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            AnchorPane carritoPane = FXMLLoader.load(getClass().getResource("/com/example/cannagrow/carrito.fxml"));
            rootPane.setCenter(carritoPane);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista del carrito.");
        }
    }

    @FXML
    private void onPedidosClick() {
        resetearBotonesMenu();
        pedidosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
        mostrarMensaje("Pedidos", "Aquí podrás revisar tus pedidos.");
    }

    @FXML
    private void onLogoutClick(javafx.event.ActionEvent event) {
        mostrarMensaje("Cerrar sesión", "Sesión cerrada. Vuelve pronto.");
        Session.setUsuarioActual(null);
        Session.cerrarSesion();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene("/com/example/cannagrow/hello-view.fxml", stage); // Te lleva de vuelta al login
    }

    @FXML
    private void onAdminClick(javafx.event.ActionEvent event) {
        mostrarMensaje("Admin", "Bienvenido Administrador de Cannagrow.");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene("/com/example/cannagrow/menu-admin.fxml", stage);
    }

    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarMensaje(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}