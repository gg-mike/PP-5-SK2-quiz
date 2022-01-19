module put.edu.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;

    requires com.google.gson;
    requires lombok;

    opens put.edu.gui.controllers to javafx.fxml;

    exports put.edu.gui;
}
