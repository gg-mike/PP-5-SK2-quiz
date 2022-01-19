package put.edu.gui.game.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import put.edu.gui.serverapi.Message;

@Data
@AllArgsConstructor
public class HeartBeat implements Message {
    private String gameNumber;
}
