package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class DeclineMessage extends Message {
    public DeclineMessage() {
        super(MessageType.DECLINE.getValue());
    }
}
