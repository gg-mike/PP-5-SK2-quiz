package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;
import put.edu.gui.game.models.Question;

import java.util.List;

public class AddQuestionsMessage extends Message {
    private final List<Question> questions;

    public AddQuestionsMessage(List<Question> questions) {
        super(MessageType.ADD_QUESTIONS.getValue());
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
