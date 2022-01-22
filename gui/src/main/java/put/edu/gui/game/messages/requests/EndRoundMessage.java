package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class EndRoundMessage extends Message {
    public EndRoundMessage() {
        super(MessageType.END_ROUND.getValue());
    }
}
