package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class CreateGameMessage extends Message {
    public CreateGameMessage() {
        super(MessageType.CREATE_GAME.getValue());
    }
}
