package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageType;

public class JoinGameMessage extends Message {
    private final int gameCode;
    private final String nick;

    public JoinGameMessage(int gameCode, String nick) {
        super(MessageType.JOIN_GAME.getValue());
        this.gameCode = gameCode;
        this.nick = nick;
    }

    public int getGameCode() {
        return gameCode;
    }

    public String getNick() {
        return nick;
    }
}


