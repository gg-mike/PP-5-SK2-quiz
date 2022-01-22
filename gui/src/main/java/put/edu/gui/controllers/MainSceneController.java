package put.edu.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import put.edu.gui.KahootApp;

public class MainSceneController {
    @FXML
    public GridPane optionsGridPane;
    @FXML
    public Button connectButton;
    @FXML
    public TextField serverAddressTextField;

    @FXML
    public void connectToServer() {
        if (KahootApp.get().isConnected()) {
            KahootApp.get().disconnect();
            return;
        }
        String serverUrl = serverAddressTextField.textProperty().getValue();
        System.out.println(serverUrl);
        if (serverUrl.isBlank()) {
            return;
        }
        String[] addressPort = serverUrl.split(":", 2);
        String address = addressPort[0];
        int port = Integer.parseInt(addressPort[1]);
        if (KahootApp.get().connect(address, port)) {
            connectButton.setText("DISCONNECT");
            serverAddressTextField.setVisible(false);
            optionsGridPane.setVisible(true);
        } else {
            KahootApp.get().showPopupWindow("can't connect", "connection to server failed");
        }
    }

    @FXML
    public void showPlayerScene() {
        KahootApp.get().showScene("player-view.fxml");
    }

    @FXML
    public void showHostScene() {
        KahootApp.get().showScene("host-view.fxml");
    }


}
