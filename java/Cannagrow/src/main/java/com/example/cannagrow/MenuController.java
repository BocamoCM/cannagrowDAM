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
    private FlowPane contenedorCategorias;
    @FXML
    private BorderPane rootPane;


    @FXML
    public void initialize() {
        System.out.println("Iniciando inicialización de MenuController...");

        // Primero verificamos si los elementos FXML están correctamente inyectados
        if (logoImage == null) {
            System.err.println("ERROR: logoImage es null");
        }

        UsuarioModel usuario = Session.getUsuarioActual();

        // Cargar logo primero
        cargarFotoPerfil();

        // Actualizar contador del carrito
        actualizarContadorCarrito();

        if (usuario != null) {
            // Aseguramos que el rol nunca sea null para evitar NPE
            String rol = (usuario.getRol() != null) ? usuario.getRol().toLowerCase() : "cliente";
            String tipoUsuario = capitalizar(rol);
            System.out.println("Usuario actual: " + tipoUsuario);

            if (rol.equals("cliente")) {
                // CLIENTE
                carritoButton.setVisible(true);
                pedidosButton.setVisible(true);
                productosButton.setVisible(true);
                adminButton.setVisible(false);
            } else {
                // EMPLEADO
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
            // Configuración para usuario no logueado
            carritoButton.setVisible(false);
            pedidosButton.setVisible(false);
            adminButton.setVisible(false);
        }

        // Cargar la vista de inicio por defecto
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/inicio.fxml"));
            Parent inicioPane = loader.load();

            if (rootPane != null) {
                rootPane.setCenter(inicioPane);
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

    private void cargarFotoPerfil() {
        try {
            UsuarioModel usuario = Session.getUsuarioActual();

            if (usuario != null && usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().isEmpty()) {
                // Intentar cargar la foto de perfil del usuario
                String fotoPerfilUrl = usuario.getFotoPerfilUrl();
                System.out.println("Intentando cargar foto de perfil desde: " + fotoPerfilUrl);

                InputStream fotoStream = getClass().getResourceAsStream(fotoPerfilUrl);

                if (fotoStream != null) {
                    logoImage.setImage(new Image(fotoStream));
                    // Configurar el ImageView para mostrar la foto en círculo
                    logoImage.setStyle("-fx-background-radius: 50%; -fx-background-color: white;");
                    System.out.println("Foto de perfil cargada correctamente");
                    return;
                } else {
                    System.err.println("No se pudo encontrar la foto de perfil en: " + fotoPerfilUrl);

                    // Si la foto no se encuentra como recurso, intentar cargar como ruta absoluta o URL
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

            // Si no hay usuario logueado o no se pudo cargar la foto, cargar logo por defecto
            cargarLogoPorDefecto();

        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            e.printStackTrace();
            // Si ocurre algún error, intentar cargar el logo por defecto
            cargarLogoPorDefecto();
        }
    }



    private void cargarLogoPorDefecto() {
        try {
            // Registrar la ruta que estamos intentando cargar
            String logoPath = "/com/example/cannagrow/cannagrow_logo.png";
            System.out.println("Cargando logo por defecto desde: " + logoPath);

            InputStream logoStream = getClass().getResourceAsStream(logoPath);
            if (logoStream != null) {
                logoImage.setImage(new Image(logoStream));
                // Resetear el estilo para el logo
                logoImage.setStyle("");
                System.out.println("Logo por defecto cargado correctamente");
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
            System.err.println("Error al cargar el logo por defecto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para capitalizar el rol (ej. "gerente" → "Gerente")
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }



    @FXML
    private void onInicioClick(javafx.event.ActionEvent event) {
        try {
            resetearBotonesMenu();
            inicioButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Cargar la vista de inicio
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/inicio.fxml"));
            Parent inicioPane = loader.load();

            // Usar el BorderPane principal para mostrar la vista
            if (rootPane != null) {
                rootPane.setCenter(inicioPane);
            } else {
                mostrarMensaje("Error", "No se pudo cargar la vista de inicio: BorderPane no encontrado.");
            }
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

            AnchorPane productosPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/Productos.fxml")));

            // Usar rootPane para mostrar la vista
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

            AnchorPane carritoPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/cannagrow/carrito.fxml")));

            // Usar rootPane para mostrar la vista
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
            // Verificar si hay un usuario en sesión
            UsuarioModel usuario = Session.getUsuarioActual();
            if (usuario == null) {
                mostrarMensaje("Iniciar sesión",
                        "Por favor, inicia sesión para acceder a los pedidos.",
                        Alert.AlertType.INFORMATION);
                return;
            }

            resetearBotonesMenu();
            pedidosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");

            // Aquí se cargaría la vista de pedidos cuando esté implementada
            // Por ahora solo mostramos un mensaje
            mostrarMensaje("Pedidos", "Aquí podrás revisar tus pedidos.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo acceder a los pedidos.");
        }
    }

    @FXML
    private void onLogoutClick(javafx.event.ActionEvent event) {
        mostrarMensaje("Cerrar sesión", "Sesión cerrada. Vuelve pronto.");

        Session.cerrarSesion(); // <-- Esto ahora también actualiza en la BD

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneChanger.changeScene("/com/example/cannagrow/hello-view.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo volver a la pantalla de inicio de sesión.");
        }
    }


    /**
     * Cierra la sesión del usuario y redirige a la pantalla de inicio de sesión.
     */

    @FXML
    private void onAdminClick(javafx.event.ActionEvent event) {
        try {
            // Verificar si el usuario tiene permiso de administrador
            UsuarioModel usuario = Session.getUsuarioActual();
            if (usuario == null || usuario.getRol() == null || !usuario.getRol().equalsIgnoreCase("gerente")) {
                mostrarMensaje("Acceso denegado",
                        "No tienes permisos para acceder al panel de administración.",
                        Alert.AlertType.WARNING);
                return;
            }

            mostrarMensaje("Admin", "Bienvenido Administrador de Cannagrow.");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneChanger.changeScene("/com/example/cannagrow/menu-admin.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo acceder al panel de administración.");
        }
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