module com.example.cannagrow {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires java.desktop;
    requires java.logging;
    requires java.naming;
    requires mysql.connector.java;  // Asegúrate de que este nombre sea correcto

    opens com.example.cannagrow to javafx.fxml;
    exports com.example.cannagrow;
}
