package put.edu.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.MessageType;
import put.edu.gui.game.messages.requests.AddQuestionsMessage;
import put.edu.gui.game.messages.requests.RequestCreateGameMessage;
import put.edu.gui.game.messages.requests.StartGameMessage;
import put.edu.gui.game.messages.responses.CreateGameMessage;
import put.edu.gui.game.models.Question;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class HostViewController {
    @FXML
    public Button createGameButton;
    @FXML
    public Button startGameButton;
    @FXML
    public Button selectFileButton;
    @FXML
    public Button nextQuestionButton;
    @FXML
    public Text gameCodeText;
    @FXML
    public Text playersText;
    @FXML
    public Text answersField;

    @FXML
    public void createGame() {
        KahootApp.get().sendMessage(new RequestCreateGameMessage());
        if (KahootApp.get().getMessageObservable().isPresent()) {
            Disposable result = KahootApp.get().getMessageObservable().get()
                    .filter(message -> (message.getType() & MessageType.CREATE_GAME.getValue()) == MessageType.CREATE_GAME.getValue())
                    .take(1)
                    .subscribe(message -> {
                        if (message instanceof CreateGameMessage createGameMessage) {
                            System.out.println("Game created");
                            gameCodeText.setText("Game code: " + createGameMessage.getGameCode());
                            createGameButton.setVisible(false);
                            selectFileButton.setVisible(true);
                        } else {
                            System.out.println("Game creation failed");
                        }
                    }, throwable -> {
                    });
        }
    }

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select questions file");
        File file = fileChooser.showOpenDialog(KahootApp.get().getStage());
        System.out.println("selected file: " + file);
        List<Question> questionList = null;
        try {
            String questionsString = Files.readString(file.toPath());
            System.out.println("questions file:\n" + questionsString);
            Type questionListType = new TypeToken<List<Question>>() {
            }.getType();
            questionList = new Gson().fromJson(questionsString, questionListType);
        } catch (Exception e) {
            System.out.println("Failed to load questions");
        }
        if (Optional.ofNullable(questionList).isPresent()) {
            System.out.println("questions loaded");
            KahootApp.get().sendMessage(new AddQuestionsMessage(questionList));
            if (KahootApp.get().getMessageObservable().isPresent()) {
                Disposable result = KahootApp.get().getMessageObservable().get()
                        .filter(message -> (message.getType() & MessageType.ADD_QUESTIONS.getValue()) == MessageType.ADD_QUESTIONS.getValue())
                        .take(1)
                        .subscribe(message -> {
                            if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                                System.out.println("questions accepted by server");
                                selectFileButton.setVisible(false);
                                startGameButton.setVisible(true);
                            } else {
                                System.out.println("questions declined by server");
                            }
                        });
            }

        }
    }

    @FXML
    public void startGame() {
        KahootApp.get().sendMessage(new StartGameMessage());
    }

    @FXML
    public void nextQuestion() {

    }

    @FXML
    public void exit() {
        KahootApp.get().disconnect();
//        KahootApp.get().showScene("main-view.fxml");
    }

}
