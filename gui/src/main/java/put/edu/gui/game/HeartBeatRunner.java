package put.edu.gui.game;

public class HeartBeatRunner implements Runnable {
    private final Thread thread;

    public HeartBeatRunner() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {

    }

    public void stop() {
        this.thread.interrupt();
    }

}
