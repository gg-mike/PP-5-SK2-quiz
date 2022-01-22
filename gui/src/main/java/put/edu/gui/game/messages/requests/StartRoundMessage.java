package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class StartRoundMessage extends Message {
    public StartRoundMessage() {
        super(MessageType.START_ROUND.getValue());
    }
}
