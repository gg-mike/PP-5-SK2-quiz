package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.models.Question;

public class StartRoundMessage extends Message {
    private Question question;

    public StartRoundMessage(int type) {
        super(type);
    }

    public Question getQuestion() {
        return question;
    }
}
