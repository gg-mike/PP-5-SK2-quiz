package put.edu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import put.edu.gui.serverapi.ServerApi;

import java.io.IOException;
import java.util.Objects;


public class KahootApplication extends Application {
    public static final int width = 800;
    public static final int height = 500;
    private static Stage stage;
    private static Scene scene;
    private static Parent parent;
    private static ServerApi serverApi;


    @Override
    public void start(Stage stage) throws IOException {
        showScene(stage, "main-view.fxml");
    }

    public static void showScene(Stage stage, String sceneFileName) throws IOException {
        parent = FXMLLoader.load(Objects.requireNonNull(KahootApplication.class.getResource(sceneFileName)));
        scene = new Scene(parent, KahootApplication.width, KahootApplication.height);
        stage.setScene(scene);
        stage.setTitle("Kahoot!");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
