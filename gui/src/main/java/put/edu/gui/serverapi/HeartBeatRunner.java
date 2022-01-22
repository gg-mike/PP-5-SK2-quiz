package put.edu.gui.serverapi;

import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.requests.HearBeatMessage;

public class HeartBeatRunner implements Runnable {
    private static final int MESSAGE_TIME = 5000;
    private final Thread thread;
    private boolean running;

    public HeartBeatRunner() {
        this.thread = new Thread(this);
        this.running = true;
        this.thread.start();
    }

    @Override
    public void run() {
        while (running) {
            KahootApp.get().sendMessage(new HearBeatMessage());
            try {
                //noinspection BusyWait
                Thread.sleep(MESSAGE_TIME);
            } catch (InterruptedException e) {
                System.out.println("HeartBeatRunner ends");
            }
        }

    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

}
