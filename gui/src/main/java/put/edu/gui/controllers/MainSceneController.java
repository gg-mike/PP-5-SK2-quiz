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
    private Stage stage;
    private Scene scene;
    private Parent parent;

    @FXML
    public void showPlayerScene(ActionEvent actionEvent) throws IOException {
        showScene(actionEvent, "player-view.fxml");
    }

    @FXML
    public void showHostScene(ActionEvent actionEvent) throws IOException {
        showScene(actionEvent, "host-view.fxml");
    }

    private void showScene(ActionEvent actionEvent, String sceneFileName) throws IOException {
        parent = FXMLLoader.load(Objects.requireNonNull(KahootApplication.class.getResource(sceneFileName)));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(parent, KahootApplication.width, KahootApplication.height);
        stage.setScene(scene);
        stage.show();
    }

}
