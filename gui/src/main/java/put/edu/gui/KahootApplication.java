package put.edu.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        FXMLLoader fxmlLoader = new FXMLLoader(KahootApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void showScene(ActionEvent actionEvent, String sceneFileName) throws IOException {
        parent = FXMLLoader.load(Objects.requireNonNull(KahootApplication.class.getResource(sceneFileName)));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(parent, KahootApplication.width, KahootApplication.height);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }



}
