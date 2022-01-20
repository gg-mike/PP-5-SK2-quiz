package put.edu.gui.game.messages;

import put.edu.gui.serverapi.Message;


public class HeartBeat extends Message {
    public HeartBeat() {
        super(HeartBeat.class.getSimpleName());
    }
}
