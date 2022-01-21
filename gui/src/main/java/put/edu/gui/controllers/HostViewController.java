package put.edu.gui.controllers;

import javafx.fxml.FXML;
import put.edu.gui.KahootApp;

import java.io.IOException;

public class HostViewController {


    @FXML
    public void exit() throws IOException {
        KahootApp.get().disconnect();
        KahootApp.get().showScene("main-view.fxml");
    }
}
