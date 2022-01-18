package put.edu.gui.serverapi;

import com.google.gson.Gson;

import java.io.BufferedWriter;

public class Writer {
    private BufferedWriter bufferedWriter;

    public Writer(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public void send(Object object) {
        String json = new Gson().toJson(object);
        System.out.println(json);
    }

}
