package put.edu.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.requests.CreateGameMessage;

import java.io.IOException;

public class HostViewController {
    @FXML
    public Text playersText;
    @FXML
    public Text answersField;

    @FXML
    public void startGame() {
        KahootApp.get().sendMessage(new CreateGameMessage());
    }

    @FXML
    public void selectFile() {

    }

    @FXML
    public void nextQuestion() {

    }

    @FXML
    public void exit() throws IOException {
        KahootApp.get().disconnect();
        KahootApp.get().showScene("main-view.fxml");
    }
}
