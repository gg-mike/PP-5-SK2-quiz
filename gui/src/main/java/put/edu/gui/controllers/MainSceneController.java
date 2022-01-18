package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import put.edu.gui.KahootApplication;

import java.io.IOException;
import java.util.Objects;


public class MainSceneController {


    @FXML
    public void showPlayerScene(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene(actionEvent, "player-view.fxml");
    }

    @FXML
    public void showHostScene(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene(actionEvent, "host-view.fxml");
    }


}
