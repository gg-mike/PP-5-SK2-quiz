package put.edu.gui.serverapi;

import java.io.IOException;
import java.net.Socket;

public class ServerApi {
    private Socket socket;
    private Reader reader;
    private Writer writer;

    public boolean connect(String address, int port) {
        System.out.printf("connecting to address: %s, port: %d%n", address, port);
        try {
            socket = new Socket(address, port);
            reader = new Reader(socket.getInputStream());
            writer = new Writer(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("connection failed");
            socket = null;
            return false;
        }
        System.out.println("connection succeeded");
        return true;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void disconnect() {
        System.out.println("disconnecting");
        if (socket != null) {
            reader.stop();
            writer.stop();
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("socket disconnection error");
            }
            reader = null;
            writer = null;
            socket = null;
        }
        System.out.println("disconnecting succeeded");
    }


}
