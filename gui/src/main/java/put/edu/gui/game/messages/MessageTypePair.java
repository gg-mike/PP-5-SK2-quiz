package put.edu.gui.game.messages;


public enum MessageTypePair {
    AllAnsweredMessage(MessageType.ALL_ANSWERED.getValue()),
    CreateGameMessage(MessageType.CREATE_GAME.getValue());

    private final int messageType;

    MessageTypePair(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }
}
