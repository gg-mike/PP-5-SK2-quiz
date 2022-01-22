package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class ResponseMessage extends Message {
    public ResponseMessage(int type) {
        super(type);
    }

    public ResponseMessage(int type, String desc) {
        super(type, desc);
    }

}
