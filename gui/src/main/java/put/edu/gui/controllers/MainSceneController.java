package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import put.edu.gui.KahootApplication;
import put.edu.gui.serverapi.ServerApi;

import java.io.IOException;


public class MainSceneController {
    @FXML
    public TextField serverAddressTextField;


    @FXML
    public void connectToServer() {
        String serverUrl = serverAddressTextField.textProperty().getValue();
        if (!serverUrl.isBlank()) {
            System.out.println(serverUrl);
            String[] addressPort = serverUrl.split(":", 2);
            String address = addressPort[0];
            int port = Integer.parseInt(addressPort[1]);
            ServerApi.connect(address, port);
        }
    }

    @FXML
    public void showPlayerScene(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene(actionEvent, "player-view.fxml");
    }

    @FXML
    public void showHostScene(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene(actionEvent, "host-view.fxml");
    }


}
