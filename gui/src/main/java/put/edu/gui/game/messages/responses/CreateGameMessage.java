package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;

public class CreateGameMessage extends Message {
    private final int gameCode;

    public CreateGameMessage(int type, int gameCode) {
        super(type);
        this.gameCode = gameCode;
    }

    public int getGameCode() {
        return gameCode;
    }

    @Override
    public String toString() {
        return "CreateGameMessage{" +
                "gameCode=" + gameCode +
                '}';
    }
}
