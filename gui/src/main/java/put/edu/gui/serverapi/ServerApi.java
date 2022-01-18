package put.edu.gui.serverapi;

import java.io.IOException;
import java.net.Socket;

public class ServerApi {
    private static String serverAddress;
    private static Socket socket;

    public static boolean connect(String address, int port) {
        System.out.printf("connecting to address: %s, port: %d%n", address, port);
        try {
            socket = new Socket(address, port);
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
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("socket disconnection error");
            }
        }
    }


}
