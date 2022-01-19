package put.edu.gui.serverapi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

@Slf4j
@Getter
public class ServerApi {
    private final Socket socket;
    private final Reader reader;
    private final Writer writer;

    public ServerApi(String address, int port) throws ConnectException {
        log.info("connecting to address: {}, port: {}", address, port);
        try {
            socket = new Socket(address, port);
            reader = new Reader(socket.getInputStream());
            writer = new Writer(socket.getOutputStream());
        } catch (IOException e) {
            throw new ConnectException("connection failed");
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void disconnect() {
        log.info("disconnecting");
        reader.stop();
        writer.stop();
        try {
            socket.close();
        } catch (IOException e) {
            log.error("socket disconnection error");
        }
        log.info("disconnecting succeeded");
    }

}
