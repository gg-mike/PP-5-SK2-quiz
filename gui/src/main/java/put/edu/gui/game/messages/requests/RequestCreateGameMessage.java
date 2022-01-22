package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class RequestCreateGameMessage extends Message {
    public RequestCreateGameMessage() {
        super(MessageType.CREATE_GAME.getValue());
    }
}
