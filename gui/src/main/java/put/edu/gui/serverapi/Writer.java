package put.edu.gui.serverapi;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Queue;

public class Writer extends ServerCommunicator {
    private final BufferedWriter bufferedWriter;

    public Writer(OutputStream outputStream) {
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public void send(Object object) throws IOException {
        String json = new Gson().toJson(object);
        System.out.println(json);
        bufferedWriter.write(json);
    }

    @Override
    public void run() {

    }

}
