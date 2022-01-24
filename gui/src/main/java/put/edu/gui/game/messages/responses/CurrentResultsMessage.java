package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class CurrentResultsMessage extends Message {
    private final int answers;

    public CurrentResultsMessage(int type, int answers) {
        super(type);
        this.answers = answers;
    }

    public int getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "CurrentResultsMessage{" +
                "numberOfAnswers=" + answers +
                '}';
    }
}
