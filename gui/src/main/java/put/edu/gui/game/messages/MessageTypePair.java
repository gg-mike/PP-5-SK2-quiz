package put.edu.gui.game.messages;

public enum MessageTypePair {
    DeclineMessage(MessageType.DECLINE.getValue()),
    AcceptMessage(MessageType.ACCEPT.getValue());

    private final int messageType;

    MessageTypePair(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }
}
