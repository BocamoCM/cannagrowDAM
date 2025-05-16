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

    /**
     * Método de inicialización llamado automáticamente por JavaFX después de cargar el FXML.
     * Inicializa el ComboBox de roles, configura los eventos de los botones y ajusta la visibilidad
     * de los campos según el rol por defecto.
     */
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

    /**
     * Configura los eventos de los componentes del formulario:
     * - Cambios en el ComboBox de roles.
     * - Click en el botón de seleccionar foto.
     * - Click en los botones de cancelar y guardar.
     */
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

    /**
     * Actualiza la visibilidad de los campos del formulario en función del rol seleccionado.
     * Los empleados ven el campo de salario, mientras que los clientes ven el campo de fecha de nacimiento.
     */
    private void actualizarVisibilidadCampos() {
        String rolSeleccionado = comboRol.getValue();
        boolean esEmpleado = !rolSeleccionado.equalsIgnoreCase("Cliente");

        // Mostrar/ocultar campos según el rol
        lblSalario.setVisible(esEmpleado);
        txtSalario.setVisible(esEmpleado);

        lblFechaNacimiento.setVisible(!esEmpleado);
        dpFechaNacimiento.setVisible(!esEmpleado);
    }

    /**
     * Abre un diálogo para seleccionar una imagen de perfil desde el sistema de archivos.
     * Simula el guardado de la imagen estableciendo una ruta por defecto basada en el rol seleccionado.
     */
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

    /**
     * Cierra el diálogo actual, recuperando la ventana desde el botón de cancelar.
     */
    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Realiza la validación de los campos y construye un modelo de usuario con los datos ingresados.
     * Llama al listener de guardado si los datos son válidos y cierra el diálogo si el guardado fue exitoso.
     */
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

    /**
     * Valida los campos del formulario:
     * - Verifica que el nombre, email y contraseña (si no es edición) no estén vacíos.
     * - Valida el formato del email.
     * - Valida el salario si el rol no es cliente.
     * - Verifica la fecha de nacimiento para clientes.
     *
     * @return true si todos los campos son válidos; false en caso contrario.
     */
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

    /**
     * Muestra una alerta de error con el mensaje proporcionado.
     *
     * @param mensaje Mensaje de error a mostrar al usuario.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Establece el modo edición del formulario.
     * Cambia el título del diálogo y ajusta el prompt del campo de contraseña.
     *
     * @param modoEdicion true para modo edición; false para modo creación.
     */
    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;

        if (modoEdicion) {
            lblTituloDialogo.setText("Editar Usuario");
            txtContrasena.setPromptText("Dejar en blanco para mantener la actual");
        } else {
            lblTituloDialogo.setText("Agregar Usuario");
        }
    }

    /**
     * Rellena los campos del formulario con los datos de un usuario existente.
     * Este método también actualiza la visibilidad de los campos según el rol del usuario.
     *
     * @param usuario Modelo de usuario cuyos datos se van a cargar en el formulario.
     */
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

    /**
     * Establece la función callback que se llamará cuando se intente guardar un usuario.
     *
     * @param listener Función que recibe el modelo de usuario y un booleano indicando si es empleado.
     *                 Debe devolver true si el guardado fue exitoso, false en caso contrario.
     */
    public void setOnGuardarListener(BiFunction<AdminUsuariosController.UsuarioTablaModel, Boolean, Boolean> listener) {
        this.onGuardarListener = listener;
    }

    /**
     * Devuelve la contraseña ingresada en el formulario, si fue modificada.
     *
     * @return Contraseña como String, o null si no fue modificada.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Devuelve la fecha de nacimiento ingresada en el formulario (solo aplicable a clientes).
     *
     * @return Fecha de nacimiento como objeto {@code java.sql.Date}, o null si no fue definida.
     */
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
}