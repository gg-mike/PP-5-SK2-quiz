package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class GameShutdownMessage extends Message {
    private int score;
    private int placeInRanking;

    public GameShutdownMessage(int type) {
        super(type);
    }

    public int getScore() {
        return score;
    }

    public int getPlaceInRanking() {
        return placeInRanking;
    }

    @Override
    public String toString() {
        return "GameShutdownMessage{" +
                "score=" + score +
                ", placeInRanking=" + placeInRanking +
                '}';
    }
}
