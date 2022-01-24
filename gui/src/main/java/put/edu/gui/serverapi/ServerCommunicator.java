package put.edu.gui.serverapi;

import io.reactivex.rxjava3.subjects.Subject;
import put.edu.gui.game.messages.Message;

public abstract class ServerCommunicator implements Runnable {
    protected static final String BEGIN_MESSAGE = "<$begin$>";
    protected static final String END_MESSAGE = "<$end$>";
    private final Thread thread;
    private final Subject<Message> messageSubject;
    private boolean running = false;

    public ServerCommunicator(Subject<Message> messageSubject) {
        this.messageSubject = messageSubject;
        this.thread = new Thread(this);
    }

    public void start() {
        running = true;
        this.thread.start();
    }

    public void stop() {
        running = false;
        this.thread.interrupt();
    }

    public Subject<Message> getMessageSubject() {
        return messageSubject;
    }

    public boolean isRunning() {
        return running;
    }


}
