package put.edu.gui.serverapi;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ServerCommunicator implements Runnable {
    private final Queue<Message> messageQueue;
    private final Thread thread;

    public ServerCommunicator() {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.thread = new Thread(this);
        this.thread.start();
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }

    public void stop() {
        this.thread.interrupt();
    }

}
