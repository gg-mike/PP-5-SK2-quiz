package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import put.edu.gui.KahootApplication;
import put.edu.gui.serverapi.ServerApi;

import java.io.IOException;


public class MainSceneController {
    @FXML
    public Button connectButton;

    @FXML
    public TextField serverAddressTextField;

    @FXML
    public void connectToServer() {
        if (ServerApi.isConnected()) {
            ServerApi.disconnect();
            connectButton.setText("CONNECT");
            serverAddressTextField.setVisible(true);
        } else {
            String serverUrl = serverAddressTextField.textProperty().getValue();
            if (!serverUrl.isBlank()) {
                System.out.println(serverUrl);
                String[] addressPort = serverUrl.split(":", 2);
                String address = addressPort[0];
                int port = Integer.parseInt(addressPort[1]);
                if (ServerApi.connect(address, port)) {
                    connectButton.setText("DISCONNECT");
                    serverAddressTextField.setVisible(false);
                }
            }
        }
    }

    @FXML
    public void showPlayerScene(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene((Stage) ((Node) actionEvent.getSource()).getScene().getWindow(), "player-view.fxml");
    }

    @FXML
    public void showHostScene(ActionEvent actionEvent) throws IOException {
        KahootApplication.showScene((Stage) ((Node) actionEvent.getSource()).getScene().getWindow(), "host-view.fxml");
    }


}
