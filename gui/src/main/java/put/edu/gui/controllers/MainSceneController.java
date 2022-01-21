package put.edu.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import put.edu.gui.KahootApp;

import java.io.IOException;

public class MainSceneController {
    @FXML
    public GridPane optionsGridPane;
    @FXML
    public Button connectButton;
    @FXML
    public TextField serverAddressTextField;

    @FXML
    public void connectToServer() {
//        if (KahootApp.get().isConnected()) {
//            KahootApp.get().disconnect();
//            connectButton.setText("CONNECT");
//            serverAddressTextField.setVisible(true);
//            optionsGridPane.setVisible(true);
//        } else {
        String serverUrl = serverAddressTextField.textProperty().getValue();
        if (!serverUrl.isBlank()) {
            System.out.println(serverUrl);
            String[] addressPort = serverUrl.split(":", 2);
            String address = addressPort[0];
            int port = Integer.parseInt(addressPort[1]);
            if (KahootApp.get().connect(address, port)) {
                connectButton.setText("DISCONNECT");
                serverAddressTextField.setVisible(false);
                optionsGridPane.setVisible(true);
            }
        }
//        }
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
