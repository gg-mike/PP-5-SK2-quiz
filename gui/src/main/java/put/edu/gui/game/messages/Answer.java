package put.edu.gui.game.messages;

import lombok.Getter;
import put.edu.gui.serverapi.Message;

@Getter
public class Answer extends Message {
    private final int question;
    private final int answer;

    public Answer(String type, int question, int answer) {
        super(Answer.class.getSimpleName());
        this.question = question;
        this.answer = answer;
    }
}
