package put.edu.gui.game.messages;


public enum MessageType {
    ACCEPT(3),
    DECLINE(2),
    ERROR(1),
    NONE(0),

    // FROM CLIENT
    CREATE_GAME(20),
    JOIN_GAME(24),
    EXIT(32),

    // FROM COMMON
    EXIT_GAME(16),
    HEARTBEAT(256),

    // TO COMMON
    SHUTDOWN(496),

    // FROM HOST
    ADD_QUESTIONS(36),
    START_GAME(68),
    END_GAME(260),
    START_ROUND(196),
    END_ROUND(388),

    // TO HOST
    ALL_ANSWERED(52),
    CURRENT_RESULTS(100),

    // FROM PLAYER
    ANSWER(40),

    // TO PLAYER
    GAME_STARTED(72),
    GAME_ENDED(264),
    GAME_SHUTDOWN(328),
    ROUND_STARTED(200),
    ROUND_ENDED(392),
    ROUND_TIMEOUT(456);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "type=" + value +
                '}';
    }

    public int getValue() {
        return value;
    }
}
