package put.edu.gui.game.messages;

public class ServerErrorMessage extends Message {
    public ServerErrorMessage() {
        super(MessageType.ERROR.getValue());
    }
}
