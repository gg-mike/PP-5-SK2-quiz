package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class RequestEndRoundMessage extends Message {
    public RequestEndRoundMessage() {
        super(MessageType.END_ROUND.getValue());
    }
}
