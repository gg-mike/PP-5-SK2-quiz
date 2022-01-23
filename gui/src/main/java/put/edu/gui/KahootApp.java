package put.edu.gui;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import put.edu.gui.controllers.PopupController;
import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.ServerErrorMessage;
import put.edu.gui.serverapi.ServerApi;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@SuppressWarnings("ALL")
public class KahootApp extends Application {
    public static final int width = 800;
    public static final int height = 500;
    private static KahootApp kahootApp;
    private Stage stage;
    private ServerApi serverApi;
    private List<Disposable> disposableList = new ArrayList<>();

    public static KahootApp get() {
        return kahootApp;
    }

    public static void main(String[] args) {
        launch();
    }

    public Stage getStage() {
        return stage;
    }

    public void showScene(String sceneFileName) {
        disposableList.stream().forEach(disposable -> disposable.dispose());
        disposableList.clear();
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(KahootApp.class.getResource(sceneFileName)));
            Scene scene = new Scene(parent, KahootApp.width, KahootApp.height);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Kahoot!");
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to show: " + sceneFileName + " scene");
        }
    }

    public void showPopupWindow(String title, String description) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(KahootApp.class.getResource("popup-view.fxml")));
            Parent parent = loader.load();
            PopupController popupController = loader.getController();
            popupController.setDescriptionText(description);
            Scene scene = new Scene(parent, 200, 100);
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(this.stage);
            popupStage.setTitle(title);
            popupStage.setScene(scene);
            popupStage.setResizable(false);
            popupStage.show();
        } catch (Exception e) {
            System.err.println("Failed to show popup window");
        }
    }

    public boolean connect(String address, int port) {
        if (Optional.ofNullable(serverApi).isPresent()) {
            System.err.println("Cannot connect when connected");
            return false;
        }
        try {
            serverApi = new ServerApi(address, port);
            System.out.println("connected");
            getMessageObservable().subscribe(message -> {
                if (message instanceof ServerErrorMessage) {
                    Platform.runLater(this::disconnect);
                }
            });
        } catch (ConnectException e) {
            System.err.println("connection failed");
            return false;
        }
        return true;
    }

    public boolean isConnected() {
        if (Optional.ofNullable(serverApi).isPresent()) {
            if (serverApi.isConnected()) {
                return true;
            }
            disconnect();
            showScene("main-view");
        }
        return false;
    }

    public void disconnect() {
        if (Optional.ofNullable(serverApi).isPresent()) {
            serverApi.disconnect();
            serverApi = null;
        }
        showScene("main-view.fxml");
    }

    public void sendMessage(Message message) {
        if (Optional.ofNullable(serverApi).isEmpty()) {
            System.out.println("cannot send message because server api is null");
            return;
        }
        serverApi.getWriter().getMessageSubject().onNext(message);
    }

    public Observable<Message> getMessageObservable() {
        if (Optional.ofNullable(serverApi).isPresent()) {
            return serverApi.getReader().getMessageSubject();
        }
        System.err.println("cannot get messages because server api is null");
        return null;
    }

    public void addDisposable(Disposable disposable) {
        disposableList.add(disposable);
    }

    @Override
    public void start(Stage stage) {
        kahootApp = this;
        this.stage = stage;
        showScene("main-view.fxml");
    }

    @Override
    public void stop() {
        disconnect();
    }

}
