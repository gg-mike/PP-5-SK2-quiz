package put.edu.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.MessageType;
import put.edu.gui.game.messages.requests.AnswerMessage;
import put.edu.gui.game.messages.requests.RequestJoinGameMessage;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PlayerSceneController {
    @FXML
    public TextField gameCodeTextField;
    @FXML
    public TextField usernameTextField;
    @FXML
    public Text infoText;
    @FXML
    public GridPane optionsGridPane;
    @FXML
    public GridPane joinGameGridPane;
    private int gameCode;


    public PlayerSceneController() {
        initSubscriptions();
    }

    @FXML
    public void joinGame() {
        try {
            String username = usernameTextField.getText();
            gameCode = Integer.parseInt(gameCodeTextField.getText());
            KahootApp.get().sendMessage(new RequestJoinGameMessage(gameCode, username));
        } catch (Exception e) {
            System.out.println("bad join game data");
            KahootApp.get().showPopupWindow("Failed to join game", "bad join game data");
        }
    }

    @FXML
    public void sendAnswer(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button button) {
            System.out.println("button: " + button.getId());
            optionsGridPane.setVisible(false);
            KahootApp.get().sendMessage(new AnswerMessage(button.getId()));
            infoText.setText("waiting for new round");
        }
    }

    @FXML
    public void exit() {
        KahootApp.get().disconnect();
    }

    private void initSubscriptions() {
        KahootApp.get().getMessageObservable()
                .filter(message -> (MessageType.JOIN_GAME.getValue() & message.getType()) == MessageType.JOIN_GAME.getValue())
                .subscribe(message -> {
                    if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                        System.out.println("joined game: " + gameCode);
                        joinGameGridPane.setVisible(false);
                        infoText.setText("joined game");
                    } else {
                        System.out.println("Failed to join game: " + gameCode);
                        KahootApp.get().showPopupWindow("Failed to join game", "Failed to join game: " + gameCode);
                    }
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> (MessageType.GAME_STARTED.getValue() & message.getType()) == MessageType.GAME_STARTED.getValue())
                .subscribe(message -> {
                    infoText.setText("game started");
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> (MessageType.ROUND_STARTED.getValue() & message.getType()) == MessageType.ROUND_STARTED.getValue())
                .subscribe(message -> {
                    infoText.setText("round started");
                    optionsGridPane.setVisible(true);
                });
    }
}
