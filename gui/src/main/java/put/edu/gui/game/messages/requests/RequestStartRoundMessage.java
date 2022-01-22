package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class RequestStartRoundMessage extends Message {
    public RequestStartRoundMessage() {
        super(MessageType.START_ROUND.getValue());
    }
}
