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

/**
 * Controlador principal del menú de la aplicación CannaGrow.
 * Gestiona la navegación y configuración de la interfaz según el rol del usuario.
 */
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

    /**
     * Inicializa el controlador de la vista Menu.
     * Configura los botones, carga información del usuario y prepara la vista inicial.
     */
    @FXML
    public void initialize() {
        System.out.println("Iniciando inicialización de MenuController...");

        // Verificar componentes clave antes de continuar
        boolean componentesValidos = verificarComponentesIU();
        if (!componentesValidos) {
            System.err.println("Algunos componentes de la UI no están disponibles. La inicialización será parcial.");
            // No salir del método para permitir inicialización parcial
        }

        // Cargar datos del usuario actual
        UsuarioModel usuario = Session.getUsuarioActual();
        if (usuario != null) {
            // Cargar nombre de usuario si el componente está disponible
            cargarnombre();

            // Cargar foto de perfil si el componente está disponible
            cargarFotoPerfil();

            // Actualizar contador del carrito si está disponible
            if (carritoButton != null) {
                actualizarContadorCarrito();
            }

            // Configurar visibilidad de botones según el rol
            configurarBotonesPorRol(usuario);
        } else {
            System.out.println("No hay usuario logueado");
            ocultarBotonesUsuarioNoLogueado();
        }

        // Cargar la vista de inicio
        cargarVistaInicio();

        System.out.println("Inicialización de MenuController completada");
    }

    /**
     * Verifica que los componentes críticos de la UI existan antes de continuar.
     *
     * @return true si todos los componentes clave están disponibles, false en caso contrario.
     */
    private boolean verificarComponentesIU() {
        boolean todosDisponibles = true;

        if (rootPane == null) {
            System.err.println("ERROR: rootPane es null");
            todosDisponibles = false;
        }

        if (userText == null) {
            System.err.println("ERROR: userText es null");
            todosDisponibles = false;
        }

        if (logoImage == null) {
            System.err.println("ERROR: logoImage es null");
            todosDisponibles = false;
        }

        return todosDisponibles;
    }

    /**
     * Configura la visibilidad de botones y el texto del usuario según el rol.
     *
     * @param usuario UsuarioModel que contiene la información del usuario actual.
     */
    private void configurarBotonesPorRol(UsuarioModel usuario) {
        if (usuario == null) return;

        String rol = (usuario.getRol() != null) ? usuario.getRol().toLowerCase() : "cliente";
        String tipoUsuario = capitalizar(rol);
        System.out.println("Usuario actual: " + tipoUsuario);

        // Solo actualizar el texto si el componente existe
        if (userText != null) {
            userText.setText("Bienvenido, " + usuario.getNombre() + " (" + tipoUsuario + ")");
        }

        // Asegurarse de que todos los botones existen antes de manipularlos
        if (carritoButton == null || pedidosButton == null ||
                productosButton == null || adminButton == null) {
            System.err.println("Algunos botones no están disponibles en esta vista");
            return;
        }

        // Configurar botones según el rol
        switch (rol) {
            case "cliente":
                carritoButton.setVisible(true);
                pedidosButton.setVisible(true);
                productosButton.setVisible(true);
                adminButton.setVisible(false);
                break;
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

    /**
     * Oculta botones cuando el usuario no está logueado
     */
    private void ocultarBotonesUsuarioNoLogueado() {
        if (carritoButton != null) carritoButton.setVisible(false);
        if (pedidosButton != null) pedidosButton.setVisible(false);
        if (adminButton != null) adminButton.setVisible(false);
    }

    /**
     * Método para cargar la vista de inicio usando cache para evitar recargas
     */
    private void cargarVistaInicio() {
        try {
            if (rootPane != null) {
                rootPane.setCenter(cargarInicioConCache());
            } else {
                System.err.println("ERROR: rootPane es null, no se puede cargar la vista de inicio");
            }
        } catch (IOException e) {
            System.err.println("Error cargando la vista de inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga la vista de inicio desde cache si está disponible, para evitar recargas.
     *
     * @return El nodo raíz de la vista de inicio.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
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

    /**
     * Carga el nombre del usuario actual y lo muestra en la etiqueta correspondiente.
     */
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

            // Verificar que el componente existe antes de manipularlo
            if (userText != null) {
                userText.setText(nombre_actual);
            } else {
                System.err.println("ERROR: No se puede establecer el nombre de usuario porque userText es null");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el nombre de usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Intenta cargar la foto de perfil del usuario actual.
     * Si no está disponible, carga un logo por defecto.
     */
    private void cargarFotoPerfil() {
        try {
            // Verificar que el componente existe antes de manipularlo
            if (logoImage == null) {
                System.err.println("ERROR: No se puede cargar la foto de perfil porque logoImage es null");
                return;
            }

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

    /**
     * Carga una imagen por defecto si no se puede cargar la foto de perfil del usuario.
     */
    private void cargarLogoPorDefecto() {
        try {
            // Verificar que el componente existe antes de manipularlo
            if (logoImage == null) {
                System.err.println("ERROR: No se puede cargar el logo por defecto porque logoImage es null");
                return;
            }

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

    /**
     * Capitaliza la primera letra de una palabra.
     *
     * @param texto Texto a capitalizar.
     * @return Texto con la primera letra en mayúscula.
     */
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    /**
     * Maneja el evento de clic en el botón de inicio.
     *
     * @param event Evento generado por el botón.
     */
    @FXML
    private void onInicioClick(javafx.event.ActionEvent event) {
        try {
            resetearBotonesMenu();

            // Verificar que el botón existe antes de manipularlo
            if (inicioButton != null) {
                inicioButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            }

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

    /**
     * Restaura el estilo por defecto de los botones del menú lateral.
     */
    private void resetearBotonesMenu() {
        String estiloNormal = "-fx-background-color: #555555; -fx-text-fill: white;";
        if (inicioButton != null) inicioButton.setStyle(estiloNormal);
        if (productosButton != null) productosButton.setStyle(estiloNormal);
        if (carritoButton != null) carritoButton.setStyle(estiloNormal);
        if (pedidosButton != null) pedidosButton.setStyle(estiloNormal);
    }

    /**
     * Maneja el evento de clic en el botón de productos.
     */
    @FXML
    public void onProductosClick() {
        try {
            resetearBotonesMenu();

            // Verificar que el botón existe antes de manipularlo
            if (productosButton != null) {
                productosButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            }

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

    /**
     * Maneja el evento de clic en el botón del carrito.
     */
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

            // Verificar que el botón existe antes de manipularlo
            if (carritoButton != null) {
                carritoButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            }

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

    /**
     * Maneja el evento de clic en el botón de pedidos.
     */
    @FXML
    private void onPedidosClick() {
        try {
            resetearBotonesMenu();

            // Verificar que el botón existe antes de manipularlo
            if (inicioButton != null) {
                inicioButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            }

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

    /**
     * Maneja el evento de clic en el botón de cerrar sesión.
     * Cierra la sesión y la ventana actual.
     */
    @FXML
    private void onLogoutClick() {
        try {
            Session.cerrarSesion();
            // Dependiendo de tu implementación, redirige a login o cierra la app
            if (logoutButton != null) {
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();
            } else {
                System.err.println("ERROR: No se puede hacer logout porque logoutButton es null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "Error al cerrar sesión.");
        }
    }

    /**
     * Muestra una alerta informativa al usuario.
     *
     * @param titulo  Título de la ventana de alerta.
     * @param mensaje Mensaje a mostrar.
     */
    public void mostrarMensaje(String titulo, String mensaje) {
        mostrarMensaje(titulo, mensaje, Alert.AlertType.INFORMATION);
    }

    /**
     * Muestra una alerta informativa al usuario.
     *
     * @param titulo  Título de la ventana de alerta.
     * @param mensaje Mensaje a mostrar.
     * @param tipo Tipo de alerta.
     */
    public void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Maneja el evento de clic en el botón de admin.
     * Abre el menu de Admin.
     */
    @FXML
    private void onAdminClick(ActionEvent event) {
        try {
            // Verificar que el evento no es null
            if (event == null || event.getSource() == null) {
                System.err.println("ERROR: Evento o fuente del evento es null en onAdminClick");
                mostrarMensaje("Error", "No se pudo cargar la vista de administración.");
                return;
            }

            // Cargar el nuevo archivo FXML de administración
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu-admin.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Crear una nueva escena con el panel de administración
            Scene scene = new Scene(root);

            // Reemplazar completamente la escena actual
            stage.setScene(scene);
            stage.show();

            System.out.println("Panel de administración cargado correctamente como nueva escena");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar la vista de administración: " + e.getMessage());
        }
    }
    /**
     * Carga el panel de administración como centro del BorderPane actual
     * en lugar de reemplazar toda la escena
     */
    private void loadAdminPanelInCurrentScene() {
        try {
            if (rootPane == null) {
                mostrarMensaje("Error", "No se puede cargar el panel de administración: BorderPane no encontrado.");
                return;
            }

            // Cargar el contenido del panel de administración
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cannagrow/menu-admin.fxml"));
            Parent adminPanel = loader.load();

            // Configurar el panel en el centro del BorderPane actual
            rootPane.setCenter(adminPanel);

            // Destacar el botón de admin
            resetearBotonesMenu();
            if (adminButton != null) {
                adminButton.setStyle("-fx-background-color: #7cb342; -fx-text-fill: white;");
            }

            System.out.println("Panel de administración cargado correctamente en la escena actual");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo cargar el contenido del panel de administración: " + e.getMessage());
        }
    }
}