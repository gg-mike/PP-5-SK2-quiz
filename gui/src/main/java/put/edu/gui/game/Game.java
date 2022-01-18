package put.edu.gui.game;

public class Game implements Runnable {
    private final Thread thread;

    public Game() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {


    }

    public void waitForServer() {

    }

    public void stop() {
        this.thread.interrupt();
    }
}
