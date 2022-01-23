package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class PlayerJoinedMessage extends Message {
    private final String nick;

    public PlayerJoinedMessage(int type, String nick) {
        super(type);
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }
}
