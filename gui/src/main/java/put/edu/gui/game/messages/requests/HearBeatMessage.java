package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class HearBeatMessage extends Message {
    public HearBeatMessage() {
        super(MessageType.HEARTBEAT.getValue());
    }
}
