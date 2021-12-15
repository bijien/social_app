module com.example.laborator6map {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.laborator6map to javafx.fxml;
    opens com.example.laborator6map.controller to javafx.fxml;
    exports com.example.laborator6map;

    opens com.example.laborator6map.domain to javafx.graphics, javafx.fxml, javafx.base;
}