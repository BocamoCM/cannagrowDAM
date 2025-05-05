module com.example.cannagrow {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;

    opens com.example.cannagrow to javafx.fxml;
    exports com.example.cannagrow;
}
