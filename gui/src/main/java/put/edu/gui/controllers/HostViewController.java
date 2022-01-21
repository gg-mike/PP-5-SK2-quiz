package put.edu.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.requests.CreateGameMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
    public void selectFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select questions file");
        File file = fileChooser.showOpenDialog(KahootApp.get().getStage());
        System.out.println("selected file: " + file);
        String questionsString = Files.readString(file.toPath());
        System.out.println("questions file:\n" + questionsString);
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
