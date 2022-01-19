package put.edu.gui.serverapi;

import io.reactivex.rxjava3.subjects.Subject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ServerCommunicator implements Runnable {
    private final Thread thread;
    private final Subject<Message> messageSubject;

    public ServerCommunicator(Subject<Message> messageSubject) {
        this.messageSubject = messageSubject;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
    }

    public Subject<Message> getMessageSubject() {
        return messageSubject;
    }
}
