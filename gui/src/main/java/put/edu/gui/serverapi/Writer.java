package put.edu.gui.serverapi;

import com.google.gson.Gson;
import io.reactivex.rxjava3.subjects.PublishSubject;
import put.edu.gui.game.messages.Message;

import java.io.IOException;
import java.io.OutputStream;

public class Writer extends ServerCommunicator {
    private final OutputStream outputStream;

    public Writer(OutputStream outputStream) {
        super(PublishSubject.create());
        this.outputStream = outputStream;
        this.start();
    }

    @Override
    public void run() {
        getMessageSubject().blockingSubscribe(this::sendMessage, throwable -> {
        });
    }

    private void sendMessage(Message message) throws IOException {
        System.out.println("Sending message: " + message);
        String json = new Gson().toJson(message);
        json = BEGIN_MESSAGE + json + END_MESSAGE + "\n";
        System.out.println(json);
        outputStream.write(json.getBytes());
    }
}
