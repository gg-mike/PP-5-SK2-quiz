package put.edu.gui.game.messages;


public abstract class Message {
    private final int type;

    public Message(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
