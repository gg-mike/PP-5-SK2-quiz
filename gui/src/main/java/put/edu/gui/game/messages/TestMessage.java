package put.edu.gui.game.messages;

import lombok.Data;
import put.edu.gui.serverapi.Message;

@Data
public class TestMessage implements Message {
    private int size;
    private String text;
}
