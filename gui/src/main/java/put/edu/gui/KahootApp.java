package put.edu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import put.edu.gui.serverapi.ServerApi;

import java.io.IOException;
import java.util.Objects;

@Getter
@Setter
public class KahootApp extends Application {
    public static final int width = 800;
    public static final int height = 500;
    private static KahootApp kahootApp;
    private Stage stage;
    private ServerApi serverApi;

    public static KahootApp get() {
        return kahootApp;
    }

    @Override
    public void start(Stage stage) throws IOException {
        kahootApp = this;
        this.stage = stage;
        showScene("main-view.fxml");
    }

    public void showScene(String sceneFileName) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(KahootApp.class.getResource(sceneFileName)));
        Scene scene = new Scene(parent, KahootApp.width, KahootApp.height);
        stage.setScene(scene);
        stage.setTitle("Kahoot!");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
