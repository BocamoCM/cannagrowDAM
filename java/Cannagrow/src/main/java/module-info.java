module com.example.cannagrow {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cannagrow to javafx.fxml;
    exports com.example.cannagrow;
    exports com.example.model;
    opens com.example.model to javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;
}