package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import put.edu.gui.KahootApp;

import java.io.IOException;


public class MainSceneController {
    @FXML
    public Button connectButton;

    @FXML
    public TextField serverAddressTextField;

    @FXML
    public void connectToServer() {
        if (KahootApp.get().getServerApi().isConnected()) {
            KahootApp.get().getServerApi().disconnect();
            connectButton.setText("CONNECT");
            serverAddressTextField.setVisible(true);
        } else {
            String serverUrl = serverAddressTextField.textProperty().getValue();
            if (!serverUrl.isBlank()) {
                System.out.println(serverUrl);
                String[] addressPort = serverUrl.split(":", 2);
                String address = addressPort[0];
                int port = Integer.parseInt(addressPort[1]);
                if (KahootApp.get().getServerApi().connect(address, port)) {
                    connectButton.setText("DISCONNECT");
                    serverAddressTextField.setVisible(false);
                }
            }
        }
    }

    @FXML
    public void showPlayerScene() throws IOException {
        KahootApp.get().showScene("player-view.fxml");
    }

    @FXML
    public void showHostScene() throws IOException {
        KahootApp.get().showScene("host-view.fxml");
    }


}
