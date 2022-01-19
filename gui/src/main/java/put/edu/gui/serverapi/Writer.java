package put.edu.gui.serverapi;

import com.google.gson.Gson;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class Writer extends ServerCommunicator {
    private final OutputStream outputStream;

    public Writer(OutputStream outputStream) {
        super(PublishSubject.create());
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        getMessageSubject().blockingSubscribe(this::sendMessage);
    }

    private void sendMessage(Message message) throws IOException {
        log.info("Sending message: {}", message);
        String json = new Gson().toJson(message);
        log.info(json);
        outputStream.write(json.getBytes());
    }

}
