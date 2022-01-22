package put.edu.gui.controllers;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.MessageType;
import put.edu.gui.game.messages.requests.AnswerMessage;
import put.edu.gui.game.messages.requests.JoinGameMessage;

import java.io.IOException;

public class PlayerSceneController {
    @FXML
    public TextField gameCodeTextField;
    @FXML
    public TextField usernameTextField;
    @FXML
    public GridPane optionsGridPane;
    @FXML
    public GridPane joinGameGridPane;
    private int gameCode;


    @FXML
    public void joinGame() {
        try {
            String username = usernameTextField.getText();
            gameCode = Integer.parseInt(gameCodeTextField.getText());
            KahootApp.get().sendMessage(new JoinGameMessage(gameCode, username));
            if (KahootApp.get().getMessageObservable().isPresent()) {
                Disposable result = KahootApp.get().getMessageObservable().get()
                        .filter(message -> message instanceof JoinGameMessage)
                        .take(1)
                        .subscribe(message -> {
                            if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                                optionsGridPane.setVisible(true);
                                joinGameGridPane.setVisible(false);
                            } else {
                                System.out.println("Failed to join game: " + gameCode);
                            }
                        });
            }
        } catch (Exception e) {
            System.out.println("bad join game data");
        }
    }

    @FXML
    public void sendAnswer(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button button) {
            System.out.println("button: " + button.getId());
            KahootApp.get().sendMessage(new AnswerMessage(0, button.getId()));
        }
    }

    @FXML
    public void exit() throws IOException {
        KahootApp.get().disconnect();
        KahootApp.get().showScene("main-view.fxml");
    }

}
