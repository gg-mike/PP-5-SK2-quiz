package put.edu.gui.game.messages;

import lombok.Getter;
import put.edu.gui.serverapi.Message;

@Getter
public class TestMessage extends Message {
    private final int size;
    private final String text;

    public TestMessage(int size, String text) {
        super(TestMessage.class.getSimpleName());
        this.size = size;
        this.text = text;
    }
}
