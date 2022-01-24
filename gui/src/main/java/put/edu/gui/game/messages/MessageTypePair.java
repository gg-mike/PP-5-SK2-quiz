package put.edu.gui.game.messages;


public enum MessageTypePair {
    CreateGameMessage(MessageType.CREATE_GAME.getValue() | MessageType.ACCEPT.getValue()),
    CurrentResultsMessage(MessageType.CURRENT_RESULTS.getValue()),
    EndRoundMessage(MessageType.END_ROUND.getValue() | MessageType.ACCEPT.getValue()),
    GameEndedMessage(MessageType.GAME_ENDED.getValue()),
    GameShutdownMessage(MessageType.GAME_SHUTDOWN.getValue()),
    JoinGameMessage(MessageType.JOIN_GAME.getValue()),
    PlayerJoinedMessage(MessageType.PLAYER_JOINED.getValue()),
    RoundEndedMessage(MessageType.ROUND_ENDED.getValue()),
    RoundTimeoutMessage(MessageType.ROUND_TIMEOUT.getValue()),
    StartRoundMessage(MessageType.START_ROUND.getValue() | MessageType.ACCEPT.getValue());

    private final int messageType;

    MessageTypePair(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageType() {
        return messageType;
    }
}
