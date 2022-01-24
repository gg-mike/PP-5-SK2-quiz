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
import put.edu.gui.game.messages.responses.*;
import put.edu.gui.game.models.Question;
import put.edu.gui.game.models.RankingRecord;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
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
    public Text rankingText;
    @FXML
    BarChart<String, Number> barChart;
    private int playerCount = 0;
    private int playerAnsweredCount = 0;
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
            int questionsCount = questionList.size();
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
        KahootApp.get().addDisposable(
                KahootApp.get().getMessageObservable()
                        .filter(message -> (message.getType() & MessageType.CREATE_GAME.getValue()) == MessageType.CREATE_GAME.getValue())
                        .subscribe(message -> {
                            if ((message.getType() & MessageType.ACCEPT.getValue()) == MessageType.ACCEPT.getValue()) {
                                System.out.println("Game created");
                                gameCodeText.setText("Game code: " + ((CreateGameMessage) message).getGameCode());
                                createGameButton.setVisible(false);
                                selectFileButton.setVisible(true);
                            } else {
                                System.out.println("Game creation failed");
                            }
                        }));
        KahootApp.get().addDisposable(
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
                        }));
        KahootApp.get().addDisposable(
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
                        }));
        KahootApp.get().addDisposable(
                KahootApp.get().getMessageObservable()
                        .filter(message -> message instanceof StartRoundMessage)
                        .subscribe(message -> {
                            StartRoundMessage startRoundMessage = (StartRoundMessage) message;
                            answersText.setText("Answers: 0");
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
                        }));
        KahootApp.get().addDisposable(
                KahootApp.get().getMessageObservable()
                        .filter(message -> (message.getType() & MessageType.ALL_ANSWERED.getValue()) == MessageType.ALL_ANSWERED.getValue())
                        .subscribe(message -> KahootApp.get().sendMessage(new RequestEndRoundMessage())));
        KahootApp.get().addDisposable(
                KahootApp.get().getMessageObservable()
                        .filter(message -> message instanceof EndRoundMessage)
                        .subscribe(message -> {
                            EndRoundMessage endRoundMessage = (EndRoundMessage) message;
                            endRoundButton.setVisible(false);
                            nextQuestionButton.setVisible(true);
                            StringBuilder ranking = new StringBuilder("Ranking: ");
                            for (RankingRecord rankingRecord : endRoundMessage.getRanking().stream().sorted(Comparator.comparingInt(RankingRecord::getPos)).limit(3).toList()) {
                                ranking.append("\n").append(rankingRecord.getPos()).append(". ").append(rankingRecord.getNick()).append(", score: ").append(rankingRecord.getScore());
                            }
                            rankingText.setText(ranking.toString());
                            XYChart.Series<String, Number> series = new XYChart.Series<>();
                            series.getData().add(new XYChart.Data<>("A", endRoundMessage.getResults().get("A")));
                            series.getData().add(new XYChart.Data<>("B", endRoundMessage.getResults().get("B")));
                            series.getData().add(new XYChart.Data<>("C", endRoundMessage.getResults().get("C")));
                            series.getData().add(new XYChart.Data<>("D", endRoundMessage.getResults().get("D")));
                            Platform.runLater(() -> updateBarChart(series));
                        }));
        KahootApp.get().addDisposable(
                KahootApp.get().getMessageObservable()
                        .filter(message -> message instanceof CurrentResultsMessage)
                        .subscribe(message -> {
                            CurrentResultsMessage currentResultsMessage = (CurrentResultsMessage) message;
                            playerAnsweredCount = currentResultsMessage.getAnswers();
                            answersText.setText("Answers: " + currentResultsMessage.getAnswers());
                        }));
        KahootApp.get().addDisposable(
                KahootApp.get().getMessageObservable()
                        .filter(message -> message instanceof PlayerJoinedMessage)
                        .subscribe(message -> {
                            playerCount++;
                            playersText.setText("Players: " + playerCount);
                        }));
    }

    private void updateBarChart(XYChart.Series<String, Number> series) {
        System.out.println("updating bar chart, data: " + series);
        barChart.getData().clear();
        barChart.getXAxis().setLabel("Answer");
        barChart.getYAxis().setLabel("Count");
        barChart.getData().add(series);
    }

}
