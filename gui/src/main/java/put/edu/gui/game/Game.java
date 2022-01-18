package put.edu.gui.game;

public class Game implements Runnable {
    private final Thread thread;
    private final int gameNumber;

    public Game(int gameNumber) {
        this.gameNumber = gameNumber;
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
