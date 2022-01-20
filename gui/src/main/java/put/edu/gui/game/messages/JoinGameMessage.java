package put.edu.gui.game.messages;

import lombok.Getter;
import put.edu.gui.serverapi.Message;

@Getter
public class JoinGameMessage extends Message {
    private final String gameNumber;

    public JoinGameMessage(String gameNumber) {
        super(JoinGameMessage.class.getSimpleName());
        this.gameNumber = gameNumber;
    }
}


