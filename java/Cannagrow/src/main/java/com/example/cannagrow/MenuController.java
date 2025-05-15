package com.example.cannagrow;

import com.example.model.Categoria;
import com.example.model.CarritoModel;
import com.example.model.Session;
import com.example.model.UsuarioModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label userText;
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
    private BorderPane rootPane;

    // Cache para la vista de inicio (y puedes agregar más caches para otras vistas)
    private static Parent inicioPaneCache = null;
    // private static Parent productosPaneCache = null;
    // private static Parent carritoPaneCache = null;
    // private static Parent pedidosPaneCache = null;

    @FXML
    public void initialize() {
        cargarnombre();
        System.out.println("Iniciando inicialización de MenuController...");

        if (logoImage == null) {
            System.err.println("ERROR: logoImage es null");
        }

        UsuarioModel usuario = Session.getUsuarioActual();

        cargarFotoPerfil();
        actualizarContadorCarrito();

        if (usuario != null) {
            String rol = (usuario.getRol() != null) ? usuario.getRol().toLowerCase() : "cliente";
            String tipoUsuario = capitalizar(rol);
            System.out.println("Usuario actual: " + tipoUsuario);
            userText.setText("Bienvenido, " + usuario.getNombre() + " (" + tipoUsuario + ")");

            if (rol.equals("cliente")) {
                carritoButton.setVisible(true);
                pedidosButton.setVisible(true);
                productosButton.setVisible(true);
                adminButton.setVisible(false);
            } else {
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
                        carritoButton.setVisible(true);
                        pedidosButton.setVisible(false);
                        productosButton.setVisible(true);
                        adminButton.setVisible(false);
                        break;
                }
            }
        } else {
            System.out.println("No hay usuario logueado");
            carritoButton.setVisible(false);
            pedidosButton.setVisible(false);
            adminButton.setVisible(false);
        }

        try {
            if (rootPane != null) {
                rootPane.setCenter(cargarInicioConCache());
            } else {
                System.err.println("ERROR: rootPane es null");
            }
        } catch (IOException e) {
            System.err.println("Error cargando la vista de inicio: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Inicialización de MenuController completada");
    }

    /**
     * Método para cargar la vista de inicio usando cache para evitar recargas
     */
    private Parent cargarInicioConCache() throws IOException {
        if (inicioPaneCache == null) {
            System.out.println("Cache de vista inicio vacía, cargando...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/inicio.fxml"));
            inicioPaneCache = loader.load();
        } else {
            System.out.println("Usando cache de vista inicio");
        }
        return inicioPaneCache;
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

    private void cargarnombre() {
        try {
            System.out.println("Cargando nombre de usuario...");
            UsuarioModel usuario = Session.getUsuarioActual();
            if (usuario == null) {
                System.out.println("No hay usuario en sesión");
                return;
            }

            String nombre_actual = usuario.getNombre();
            System.out.println("Nombre obtenido: " + nombre_actual);
            userText.setText(nombre_actual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarFotoPerfil() {
        try {
            UsuarioModel usuario = Session.getUsuarioActual();

            if (usuario != null && usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().isEmpty()) {
                String fotoPerfilUrl = usuario.getFotoPerfilUrl();
                System.out.println("Intentando cargar foto de perfil desde: " + fotoPerfilUrl);

                InputStream fotoStream = getClass().getResourceAsStream(fotoPerfilUrl);

                if (fotoStream != null) {
                    logoImage.setImage(new Image(fotoStream));
                    logoImage.setStyle("-fx-background-radius: 50%; -fx-background-color: white;");
                    System.out.println("Foto de perfil cargada correctamente");
                    return;
                } else {
                    System.err.println("No se pudo encontrar la foto de perfil en: " + fotoPerfilUrl);

                    try {
                        Image imagen = new Image(fotoPerfilUrl);
                        if (!imagen.isError()) {
                            logoImage.setImage(imagen);
                            logoImage.setStyle("-fx-background-radius: 50%; -fx-background-color: white;");
                            System.out.println("Foto de perfil cargada desde ruta absoluta o URL");
                            return;
                        }
                    } catch (Exception ex) {
                        System.err.println("Error al cargar desde ruta absoluta: " + ex.getMessage());
                    }
                }
            }

            cargarLogoPorDefecto();

        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            e.printStackTrace();
            cargarLogoPorDefecto();
        }
    }

    private void cargarLogoPorDefecto() {
        try {
            String logoPath = "/com/example/cannagrow/img/perfil_cliente.png";
            System.out.println("Cargando logo por defecto desde: " + logoPath);

            InputStream logoStream = getClass().getResourceAsStream(logoPath);
            if (logoStream != null) {
                logoImage.setImage(new Image(logoStream));
                System.out.println("Logo por defecto cargado correctamente");
            } else {
                String fallbackPath = "/com/example/cannagrow/cannagrow_logo.png";
                InputStream fallbackStream = getClass().getResourceAsStream(fallbackPath);

                if (fallbackStream != null) {
                    logoImage.setImage(new Image(fallbackStream));
                    System.out.println("Logo principal cargado como alternativa");
                } else {
                    System.err.println("No se pudo cargar ningún logo por defecto");
                }
            }

            logoImage.setStyle("");
        } catch (Exception e) {
            System.err.println("Error al cargar el logo por defecto: " + e.getMessage());
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    @FXML
    private void onInicioClick(javafx.event.ActionEvent event) {
        try {
            resetearBotonesMenu();
            inicioButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            if (rootPane != null) {
                rootPane.setCenter(cargarInicioConCache());
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de inicio: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de inicio.");
        }
    }

    private void resetearBotonesMenu() {
        String estiloNormal = "-fx-background-color: #555555; -fx-text-fill: white;";
        if (inicioButton != null) inicioButton.setStyle(estiloNormal);
        if (productosButton != null) productosButton.setStyle(estiloNormal);
        if (carritoButton != null) carritoButton.setStyle(estiloNormal);
        if (pedidosButton != null) pedidosButton.setStyle(estiloNormal);
    }

    @FXML
    public void onProductosClick() {
        try {
            resetearBotonesMenu();
            productosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Si quieres cachear productos, descomenta y usa este bloque
            // if (productosPaneCache == null) {
            //     productosPaneCache = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/Productos.fxml")));
            // }
            // if (rootPane != null) rootPane.setCenter(productosPaneCache);

            // Por ahora carga sin cache:
            AnchorPane productosPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/Productos.fxml")));

            if (rootPane != null) {
                rootPane.setCenter(productosPane);
                actualizarContadorCarrito();
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de productos: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de productos.");
        }
    }

    @FXML
    private void onCarritoClick() {
        try {
            UsuarioModel usuario = Session.getUsuarioActual();
            if (usuario == null) {
                mostrarMensaje("Iniciar sesión",
                        "Por favor, inicia sesión para acceder al carrito.",
                        Alert.AlertType.INFORMATION);
                return;

            }

            resetearBotonesMenu();
            carritoButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Cache carrito similar:
            // if (carritoPaneCache == null) {
            //     carritoPaneCache = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/carrito.fxml")));
            // }
            // if (rootPane != null) rootPane.setCenter(carritoPaneCache);

            AnchorPane carritoPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/carrito.fxml")));

            if (rootPane != null) {
                rootPane.setCenter(carritoPane);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista del carrito: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista del carrito.");
        }
    }

    @FXML
    private void onPedidosClick() {
        try {
            resetearBotonesMenu();
            inicioButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Cache pedidos similar:
            // if (pedidosPaneCache == null) {
            //     pedidosPaneCache = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/pedidos.fxml")));
            // }
            // if (rootPane != null) rootPane.setCenter(pedidosPaneCache);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/pedidos.fxml"));
            Parent inicioPane = loader.load();
            if (rootPane != null) {
                rootPane.setCenter(inicioPane);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de pedidos: BorderPane no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de pedidos.");
        }
    }

    @FXML
    private void onLogoutClick() {
        try {
            Session.cerrarSesion();
            // Dependiendo de tu implementación, redirige a login o cierra la app
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "Error al cerrar sesión.");
        }
    }

    public void mostrarMensaje(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.INFORMATION);
    }


    public void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onAdminClick(ActionEvent event) {
        try {
            // Cargar el nuevo archivo FXML
            Parent root = FXMLLoader.load(getClass().getResource("menu-admin.fxml"));

            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Establecer la nueva escena
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Opcional: Mostrar un mensaje de error
        }
    }
}

