package put.edu.gui.game.messages.requests;

import put.edu.gui.game.messages.Message;

public class TestMessage extends Message {
    private final int size;
    private final String text;

    public TestMessage(int size, String text) {
        super(0);
        this.size = size;
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public String getText() {
        return text;
    }
}
