module com.example.cannagrow {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cannagrow to javafx.fxml;
    exports com.example.cannagrow;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;
}