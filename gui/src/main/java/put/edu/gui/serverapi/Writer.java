package put.edu.gui.serverapi;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

@Slf4j
public class Writer extends ServerCommunicator {
    private final BufferedWriter bufferedWriter;

    public Writer(OutputStream outputStream) {
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public void send(Object object) throws IOException {
        String json = new Gson().toJson(object);
        log.info(json);
        bufferedWriter.write(json);
    }

    @Override
    public void run() {
        try {
            getMessageQueue().take();
        } catch (InterruptedException e) {
            log.error("take message exception");
        }
    }

}
