module com.example.cannagrow {
    // Módulos JavaFX necesarios
    requires javafx.controls;
    requires javafx.fxml;

    // API JDBC estándar
    requires java.sql;


    // Otros módulos de JDK si realmente los necesitas
    requires java.desktop;
    requires java.logging;
    requires java.naming;

    // Abre tu paquete a JavaFX para la inyección de FXML
    opens com.example.cannagrow to javafx.fxml;
    exports com.example.cannagrow;
}
