package put.edu.gui.game.messages;


public enum MessageTypePair {
    AllAnsweredMessage(MessageType.ALL_ANSWERED.getValue()),
    CreateGameMessage(MessageType.CREATE_GAME.getValue()),
    CurrentResultsMessage(MessageType.CURRENT_RESULTS.getValue()),
    GameEndedMessage(MessageType.GAME_ENDED.getValue()),
    JoinGameMessage(MessageType.JOIN_GAME.getValue()),
    RoundEndedMessage(MessageType.ROUND_ENDED.getValue()),
    RoundTimeoutMessage(MessageType.ROUND_TIMEOUT.getValue());

    private final int messageType;

    MessageTypePair(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }
}
