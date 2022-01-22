package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class GameShutdownMessage extends Message {
    public GameShutdownMessage() {
        super(MessageType.GAME_ENDED.getValue() | MessageType.ACCEPT.getValue());
    }
}
