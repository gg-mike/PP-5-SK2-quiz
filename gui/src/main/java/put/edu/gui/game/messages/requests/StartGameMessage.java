package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class StartGameMessage extends Message {
    public StartGameMessage() {
        super(MessageType.START_GAME.getValue());
    }
}
