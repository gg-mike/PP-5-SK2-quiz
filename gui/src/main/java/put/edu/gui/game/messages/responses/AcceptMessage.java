package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class AcceptMessage extends Message {
    public AcceptMessage() {
        super(MessageType.ACCEPT.getValue());
    }

}
