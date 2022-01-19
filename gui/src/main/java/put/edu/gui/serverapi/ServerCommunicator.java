package put.edu.gui.serverapi;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public abstract class ServerCommunicator implements Runnable {
    private final LinkedBlockingQueue<Message> messageQueue;
    private final Thread thread;

    public ServerCommunicator() {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.thread = new Thread(this);
        this.thread.start();

    }

    public void stop() {
        this.thread.interrupt();
    }

    public LinkedBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
