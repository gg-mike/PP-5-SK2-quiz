package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import put.edu.gui.KahootApp;
import put.edu.gui.game.Game;

import java.io.IOException;

public class PlayerSceneController {
    private Game game;
    @FXML
    public TextField gameNumberTextField;

    @FXML
    public void joinGame() {

    }

    public void sendAnswer(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "A" -> System.out.println("A");
                case "B" -> System.out.println("B");
                case "C" -> System.out.println("C");
                case "D" -> System.out.println("D");
            }
        }

    }

    @FXML
    public void exit(ActionEvent actionEvent) throws IOException {
        KahootApp.get().showScene("main-view.fxml");
    }

}
