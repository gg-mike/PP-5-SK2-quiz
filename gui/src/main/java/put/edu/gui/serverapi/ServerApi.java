package put.edu.gui.serverapi;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerApi {
    private static String serverAddress;
    private static Socket socket;
    private static Reader reader;
    private static Writer writer;

    public static boolean connect(String address, int port) {
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

    public static boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public static void disconnect() {
        System.out.println("disconnecting");
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                System.err.println("socket disconnection error");
            }
        }
        System.out.println("disconnecting succeeded");
    }


}
