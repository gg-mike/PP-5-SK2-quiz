package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import put.edu.gui.KahootApp;
import put.edu.gui.game.Game;
import put.edu.gui.game.messages.requests.JoinGameMessage;

import java.io.IOException;

public class PlayerSceneController {
    @FXML
    public TextField gameNumberTextField;
    private Game game;

    @FXML
    public void joinGame() {

    }

    public void sendAnswer(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button button) {
            System.out.println("button: " + button.getId());
            KahootApp.get().sendMessage(new JoinGameMessage(Integer.parseInt(button.getId()), "Piotr"));
        }

    }

    @FXML
    public void exit() throws IOException {
        KahootApp.get().disconnect();
        KahootApp.get().showScene("main-view.fxml");
    }

}
