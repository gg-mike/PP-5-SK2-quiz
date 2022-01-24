package put.edu.gui.game;

import put.edu.gui.serverapi.HeartBeatRunner;

public class Game {
    private final int gameNumber;
    private final HeartBeatRunner heartBeatRunner;

    public Game(int gameNumber) {
        this.gameNumber = gameNumber;
        this.heartBeatRunner = new HeartBeatRunner();
    }

    public void stop() {
        heartBeatRunner.stop();
    }

    public int getGameNumber() {
        return gameNumber;
    }
}
