package put.edu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import put.edu.gui.serverapi.ServerApi;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Objects;
import java.util.Optional;

public class KahootApp extends Application {
    public static final int width = 800;
    public static final int height = 500;
    private static KahootApp kahootApp;
    private Stage stage;
    private ServerApi serverApi;

    public static KahootApp get() {
        return kahootApp;
    }

    public static void main(String[] args) {
        launch();
    }

    public void showScene(String sceneFileName) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(KahootApp.class.getResource(sceneFileName)));
        Scene scene = new Scene(parent, KahootApp.width, KahootApp.height);
        stage.setScene(scene);
        stage.setTitle("Kahoot!");
        stage.show();
    }

    public boolean connect(String address, int port) {
        if (Optional.ofNullable(serverApi).isPresent()) {
            return false;
        }
        try {
            serverApi = new ServerApi(address, port);
            System.out.println("connected");
        } catch (ConnectException e) {
            System.err.println("connection failed");
        }
        return true;
    }

    public boolean isConnected() {
        return Optional.ofNullable(serverApi).isPresent() && serverApi.isConnected();
    }

    public void disconnect() {
        serverApi.disconnect();
    }

    @Override
    public void start(Stage stage) throws IOException {
        kahootApp = this;
        this.stage = stage;
        showScene("main-view.fxml");
    }

}
