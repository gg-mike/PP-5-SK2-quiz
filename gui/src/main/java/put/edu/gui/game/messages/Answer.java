package put.edu.gui.game.messages;

import lombok.Data;
import put.edu.gui.serverapi.Message;

@Data
public class Answer implements Message {
    private int question;
    private int answer;
}
