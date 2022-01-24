package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class RoundEndedMessage extends Message {
    private final boolean wasCorrectAnswer;
    private final int score;
    private final int placeInRanking;

    public RoundEndedMessage(int type, boolean wasCorrectAnswer, int score, int placeInRanking) {
        super(type);
        this.wasCorrectAnswer = wasCorrectAnswer;
        this.score = score;
        this.placeInRanking = placeInRanking;
    }

    public boolean isWasCorrectAnswer() {
        return wasCorrectAnswer;
    }

    public int getScore() {
        return score;
    }

    public int getPlaceInRanking() {
        return placeInRanking;
    }

    @Override
    public String toString() {
        return "RoundEndedMessage{" +
                "wasCorrectAnswer=" + wasCorrectAnswer +
                ", score=" + score +
                ", placeInRanking=" + placeInRanking +
                '}';
    }
}
