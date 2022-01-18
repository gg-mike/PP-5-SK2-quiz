package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import put.edu.gui.KahootApplication;

import java.io.IOException;

public class PlayerSceneController {

    @FXML
    public TextField gameNumberTextField;

    @FXML
    public void joinGame() {


    }

    @FXML
    public void exit(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene((Stage) ((Node) actionEvent.getSource()).getScene().getWindow(), "main-view.fxml");
    }

}
