package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class CurrentResultsMessage extends Message {
    private final int numberOfAnswers;

    public CurrentResultsMessage(int type, int numberOfAnswers) {
        super(type);
        this.numberOfAnswers = numberOfAnswers;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    @Override
    public String toString() {
        return "CurrentResultsMessage{" +
                "numberOfAnswers=" + numberOfAnswers +
                '}';
    }
}
