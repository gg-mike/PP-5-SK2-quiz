package put.edu.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.MessageType;
import put.edu.gui.game.messages.requests.*;
import put.edu.gui.game.messages.responses.AllAnsweredMessage;
import put.edu.gui.game.messages.responses.CreateGameMessage;
import put.edu.gui.game.models.Question;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ResultOfMethodCallIgnored")
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
    public Button endRoundButton;
    @FXML
    public Text gameCodeText;
    @FXML
    public Text playersText;
    @FXML
    public Text answersField;
    @FXML
    BarChart<String, Integer> barChart;
    private int questionsCount;

    public HostViewController() {
        initSubscriptions();
    }

    @FXML
    public void createGame() {
        KahootApp.get().sendMessage(new RequestCreateGameMessage());
    }

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select questions file");
        File file = fileChooser.showOpenDialog(KahootApp.get().getStage());
        System.out.println("selected file: " + file);
        List<Question> questionList = null;
        try {
            String questionsString = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            System.out.println("questions file:\n" + questionsString);
            Type questionListType = new TypeToken<List<Question>>() {
            }.getType();
            questionList = new Gson().fromJson(questionsString, questionListType);
        } catch (Exception e) {
            System.out.println("Failed to load questions");
        }
        if (Optional.ofNullable(questionList).isPresent()) {
            questionsCount = questionList.size();
            System.out.println("questions loaded");
            KahootApp.get().sendMessage(new AddQuestionsMessage(questionList));

        }
    }

    @FXML
    public void startGame() {
        KahootApp.get().sendMessage(new StartGameMessage());
        startGameButton.setVisible(false);
        nextQuestionButton.setVisible(true);
        endRoundButton.setVisible(true);
    }

    @FXML
    public void nextQuestion() {
        KahootApp.get().sendMessage(new StartRoundMessage());
    }

    @FXML
    public void endRound() {
        KahootApp.get().sendMessage(new EndRoundMessage());
    }

    @FXML
    public void exit() {
        KahootApp.get().disconnect();
    }

    private void initSubscriptions() {
        KahootApp.get().getMessageObservable()
                .filter(message -> (message.getType() & MessageType.CREATE_GAME.getValue()) == MessageType.CREATE_GAME.getValue())
                .subscribe(message -> {
                    if (message instanceof CreateGameMessage createGameMessage) {
                        System.out.println("Game created");
                        gameCodeText.setText("Game code: " + createGameMessage.getGameCode());
                        createGameButton.setVisible(false);
                        selectFileButton.setVisible(true);
                    } else {
                        System.out.println("Game creation failed");
                    }
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> (message.getType() & MessageType.ADD_QUESTIONS.getValue()) == MessageType.ADD_QUESTIONS.getValue())
                .subscribe(message -> {
                    if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                        System.out.println("questions accepted by server");
                        selectFileButton.setVisible(false);
                        startGameButton.setVisible(true);
                    } else {
                        System.out.println("questions declined by server");
                    }
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> message instanceof AllAnsweredMessage)
                .subscribe(message -> {
                    XYChart.Series<String, Integer> series = new XYChart.Series<>();

                    series.getData().add(new XYChart.Data<>("A", ((AllAnsweredMessage) message).getA()));
                    series.getData().add(new XYChart.Data<>("B", ((AllAnsweredMessage) message).getB()));
                    series.getData().add(new XYChart.Data<>("C", ((AllAnsweredMessage) message).getC()));
                    series.getData().add(new XYChart.Data<>("D", ((AllAnsweredMessage) message).getD()));

                    barChart.getData().clear();
                    barChart.getData().add(series);

                });
    }

}
