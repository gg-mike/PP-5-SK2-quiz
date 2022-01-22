package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class AnswerMessage extends Message {
    private final String answer;

    public AnswerMessage(String answer) {
        super(MessageType.ANSWER.getValue());
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
