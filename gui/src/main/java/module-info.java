module put.edu.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;

    requires com.google.gson;
    requires io.reactivex.rxjava3;

    opens put.edu.gui.controllers to javafx.fxml;
    opens put.edu.gui.game.messages to com.google.gson;
    opens put.edu.gui to com.google.gson;

    opens put.edu.gui.game.messages.requests to com.google.gson;


    exports put.edu.gui;
    exports put.edu.gui.serverapi;
    exports put.edu.gui.game.messages;
}
