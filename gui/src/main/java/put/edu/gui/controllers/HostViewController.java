package put.edu.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.MessageType;
import put.edu.gui.game.messages.requests.*;
import put.edu.gui.game.messages.responses.CreateGameMessage;
import put.edu.gui.game.messages.responses.CurrentResultsMessage;
import put.edu.gui.game.messages.responses.EndRoundMessage;
import put.edu.gui.game.messages.responses.StartRoundMessage;
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
    public Text answersText;
    @FXML
    public Text questionText;
    @FXML
    BarChart<String, Number> barChart;
    private int playerCount = 0;
    private int playerAnsweredCount = 0;
    private int questionsCount = 0;
    private int questionNumber;

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
    }

    @FXML
    public void nextQuestion() {
        KahootApp.get().sendMessage(new RequestStartRoundMessage());
    }

    @FXML
    public void endRound() {
        KahootApp.get().sendMessage(new RequestEndRoundMessage());
    }

    @FXML
    public void exit() {
        KahootApp.get().disconnect();
    }

    private void initSubscriptions() {
        KahootApp.get().getMessageObservable()
                .filter(message -> (message.getType() & MessageType.CREATE_GAME.getValue()) == MessageType.CREATE_GAME.getValue())
                .take(1)
                .subscribe(message -> {
                    if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                        System.out.println("Game created");
                        gameCodeText.setText("Game code: " + ((CreateGameMessage) message).getGameCode());
                        createGameButton.setVisible(false);
                        selectFileButton.setVisible(true);
                    } else {
                        System.out.println("Game creation failed");
                    }
                });
        KahootApp.get().getMessageObservable()
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
        KahootApp.get().getMessageObservable()
                .filter(message -> (message.getType() & MessageType.START_GAME.getValue()) == MessageType.START_GAME.getValue())
                .subscribe(message -> {
                    if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                        System.out.println("game started");
                        startGameButton.setVisible(false);
                        nextQuestionButton.setVisible(true);
                    } else if ((message.getType() & MessageType.DECLINE.getValue()) == MessageType.DECLINE.getValue()) {
                        System.out.println("start game declined");
                        Platform.runLater(() -> KahootApp.get().showPopupWindow("start game declined", message.getDesc()));
                    }
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> message instanceof StartRoundMessage)
                .subscribe(message -> {
                    StartRoundMessage startRoundMessage = (StartRoundMessage) message;
                    questionText.setText("Question: " +
                            "\nnumber: " + startRoundMessage.getQuestion().getN() +
                            "\ncontents: " + startRoundMessage.getQuestion().getQ() +
                            "\nAnswers: " +
                            "\nA: " + startRoundMessage.getQuestion().getA() +
                            "\nB: " + startRoundMessage.getQuestion().getB() +
                            "\nC: " + startRoundMessage.getQuestion().getC() +
                            "\nD: " + startRoundMessage.getQuestion().getD()
                    );
                    endRoundButton.setVisible(true);
                    nextQuestionButton.setVisible(false);
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> (message.getType() & MessageType.ALL_ANSWERED.getValue()) == MessageType.ALL_ANSWERED.getValue())
                .subscribe(message -> {
                    endRoundButton.setVisible(false);
                    nextQuestionButton.setVisible(true);
                    KahootApp.get().sendMessage(new RequestEndRoundMessage());
                });

        KahootApp.get().getMessageObservable()
                .filter(message -> message instanceof EndRoundMessage)
                .subscribe(message -> {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.getData().add(new XYChart.Data<>("A", ((EndRoundMessage) message).getA()));
                    series.getData().add(new XYChart.Data<>("B", ((EndRoundMessage) message).getB()));
                    series.getData().add(new XYChart.Data<>("C", ((EndRoundMessage) message).getC()));
                    series.getData().add(new XYChart.Data<>("D", ((EndRoundMessage) message).getD()));
                    Platform.runLater(() -> updateBarChart(series));
                });
        KahootApp.get().getMessageObservable()
                .filter(message -> message instanceof CurrentResultsMessage)
                .subscribe(message -> {
                    CurrentResultsMessage currentResultsMessage = (CurrentResultsMessage) message;
                    playerAnsweredCount = currentResultsMessage.getNumberOfAnswers();
                    answersText.setText("Answers: " + currentResultsMessage.getNumberOfAnswers());
                });
    }

    private void updateBarChart(XYChart.Series<String, Number> series) {
        barChart.getData().clear();
        barChart.getXAxis().setLabel("Answer");
        barChart.getYAxis().setLabel("Count");
        barChart.getData().add(series);
    }

}
