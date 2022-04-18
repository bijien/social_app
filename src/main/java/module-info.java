module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;


    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    exports com.example;

    opens com.example.domain to javafx.graphics, javafx.fxml, javafx.base;
}