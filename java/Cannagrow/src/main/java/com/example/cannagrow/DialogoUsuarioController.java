package com.example.cannagrow;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.function.BiFunction;

public class DialogoUsuarioController {

    @FXML
    private Label lblTituloDialogo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    private TextField txtSalario;

    @FXML
    private Label lblSalario;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private Label lblFechaNacimiento;

    @FXML
    private TextField txtFotoUrl;

    @FXML
    private Button btnSeleccionarFoto;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    private boolean modoEdicion = false;
    private AdminUsuariosController.UsuarioTablaModel usuarioActual;
    private BiFunction<AdminUsuariosController.UsuarioTablaModel, Boolean, Boolean> onGuardarListener;
    private String contrasena;
    private Date fechaNacimiento;

    @FXML
    public void initialize() {
        // Inicializar el combo de roles
        comboRol.setItems(FXCollections.observableArrayList("Cliente", "Gerente", "Vendedor", "Almacenista"));
        comboRol.setValue("Cliente");

        // Configurar eventos
        configurarEventos();

        // Configurar visibilidad inicial
        actualizarVisibilidadCampos();
    }

    private void configurarEventos() {
        // Cuando cambia el rol, actualizar la visibilidad de campos
        comboRol.setOnAction(event -> actualizarVisibilidadCampos());

        // Cuando se hace clic en el botón de seleccionar foto
        btnSeleccionarFoto.setOnAction(event -> seleccionarFoto());

        // Cuando se hace clic en cancelar
        btnCancelar.setOnAction(event -> cerrarDialogo());

        // Cuando se hace clic en guardar
        btnGuardar.setOnAction(event -> guardarUsuario());
    }

    private void actualizarVisibilidadCampos() {
        String rolSeleccionado = comboRol.getValue();
        boolean esEmpleado = !rolSeleccionado.equalsIgnoreCase("Cliente");

        // Mostrar/ocultar campos según el rol
        lblSalario.setVisible(esEmpleado);
        txtSalario.setVisible(esEmpleado);

        lblFechaNacimiento.setVisible(!esEmpleado);
        dpFechaNacimiento.setVisible(!esEmpleado);
    }

    private void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(btnSeleccionarFoto.getScene().getWindow());
        if (selectedFile != null) {
            // Aquí normalmente copiarías el archivo a una carpeta de recursos y guardarías la ruta relativa
            // Por simplicidad, simularemos que la imagen se guarda en la ruta predeterminada
            String rolSeleccionado = comboRol.getValue();
            String rutaImagen;

            if (rolSeleccionado.equalsIgnoreCase("Cliente")) {
                rutaImagen = "/com/example/cannagrow/img/perfil_cliente.png";
            } else {
                rutaImagen = "/com/example/cannagrow/img/perfil_" + rolSeleccionado.toLowerCase() + ".png";
            }

            txtFotoUrl.setText(rutaImagen);
        }
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void guardarUsuario() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Crear el modelo de usuario
        AdminUsuariosController.UsuarioTablaModel usuario = modoEdicion ? usuarioActual : new AdminUsuariosController.UsuarioTablaModel();
        usuario.setNombre(txtNombre.getText().trim());
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setRol(comboRol.getValue());

        boolean esEmpleado = !comboRol.getValue().equalsIgnoreCase("Cliente");

        if (esEmpleado) {
            try {
                usuario.setSalario(Double.parseDouble(txtSalario.getText().trim()));
            } catch (NumberFormatException e) {
                usuario.setSalario(0.0);
            }
            usuario.setEsEmpleado(true);
        } else {
            usuario.setSalario(0.0);
            usuario.setEsEmpleado(false);

            // Guardar la fecha para el registro de cliente
            if (dpFechaNacimiento.getValue() != null) {
                fechaNacimiento = Date.valueOf(dpFechaNacimiento.getValue());
            }
        }

        // Guardar la foto de perfil
        usuario.setFotoPerfilUrl(txtFotoUrl.getText().trim());

        // Guardar la contraseña para el registro
        if (!txtContrasena.getText().isEmpty()) {
            contrasena = txtContrasena.getText();
        }

        // Llamar al listener de guardado
        if (onGuardarListener != null) {
            boolean resultado = onGuardarListener.apply(usuario, esEmpleado);
            if (resultado) {
                cerrarDialogo();
            }
        }
    }

    private boolean validarCampos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        // Validar email
        if (txtEmail.getText().trim().isEmpty() || !txtEmail.getText().contains("@")) {
            mostrarError("Email inválido");
            return false;
        }

        // Validar contraseña si no estamos en modo edición
        if (!modoEdicion && txtContrasena.getText().isEmpty()) {
            mostrarError("La contraseña es obligatoria");
            return false;
        }

        // Validar salario para empleados
        if (!comboRol.getValue().equalsIgnoreCase("Cliente")) {
            try {
                double salario = Double.parseDouble(txtSalario.getText().trim());
                if (salario < 0) {
                    mostrarError("El salario debe ser un valor positivo");
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarError("El salario debe ser un valor numérico");
                return false;
            }
        } else {
            // Validar fecha para clientes
            if (dpFechaNacimiento.getValue() == null) {
                mostrarError("La fecha de nacimiento es obligatoria para clientes");
                return false;
            }
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;

        if (modoEdicion) {
            lblTituloDialogo.setText("Editar Usuario");
            txtContrasena.setPromptText("Dejar en blanco para mantener la actual");
        } else {
            lblTituloDialogo.setText("Agregar Usuario");
        }
    }

    public void setUsuario(AdminUsuariosController.UsuarioTablaModel usuario) {
        this.usuarioActual = usuario;

        // Llenar los campos con los datos del usuario
        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        comboRol.setValue(usuario.getRol());

        if (usuario.isEsEmpleado()) {
            txtSalario.setText(String.valueOf(usuario.getSalario()));
        } else {
            // Aquí podrías cargar la fecha de nacimiento del cliente desde la base de datos
            dpFechaNacimiento.setValue(LocalDate.now());
        }

        txtFotoUrl.setText(usuario.getFotoPerfilUrl());

        // Actualizar visibilidad según el rol
        actualizarVisibilidadCampos();
    }

    public void setOnGuardarListener(BiFunction<AdminUsuariosController.UsuarioTablaModel, Boolean, Boolean> listener) {
        this.onGuardarListener = listener;
    }

    public String getContrasena() {
        return contrasena;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
}