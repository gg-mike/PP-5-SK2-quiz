package put.edu.gui.game.messages;


public enum MessageTypePair {
    DeclineMessage(0);

    private final int messageType;

    MessageTypePair(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }
}
