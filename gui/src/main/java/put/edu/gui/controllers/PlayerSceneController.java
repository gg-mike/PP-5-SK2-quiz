package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import put.edu.gui.KahootApp;
import put.edu.gui.game.Game;
import put.edu.gui.game.messages.HeartBeat;

import java.io.IOException;

@Slf4j
public class PlayerSceneController {
    @FXML
    public TextField gameNumberTextField;
    private Game game;

    @FXML
    public void joinGame() {

    }

    public void sendAnswer(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button button) {
            log.info("button: " + button.getId());
            KahootApp.get().sendMessage(new HeartBeat("button: " + button.getId()));
        }

    }

    @FXML
    public void exit(ActionEvent actionEvent) throws IOException {
        KahootApp.get().showScene("main-view.fxml");
    }

}
