package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class AnswerMessage extends Message {
    private final int question;
    private final String answer;

    public AnswerMessage(int question, String answer) {
        super(MessageType.ANSWER.getValue());
        this.question = question;
        this.answer = answer;
    }

    public int getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
