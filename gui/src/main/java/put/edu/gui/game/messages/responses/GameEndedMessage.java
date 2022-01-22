package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class GameEndedMessage extends Message {
    private final int score;
    private final int placeInRanking;

    public GameEndedMessage(int type, int score, int placeInRanking) {
        super(type);
        this.score = score;
        this.placeInRanking = placeInRanking;
    }

    public int getScore() {
        return score;
    }

    public int getPlaceInRanking() {
        return placeInRanking;
    }
}
