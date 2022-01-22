package put.edu.gui.game.messages;


public abstract class Message {
    private final int type;
    private String desc = "";

    public Message(int type) {
        this.type = type;
    }

    public Message(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static String resolveTypeName(int type) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MessageType messageType : MessageType.values()) {
            if ((messageType.getValue() & type) == messageType.getValue()) {
                stringBuilder.append(messageType).append(',');
            }
        }
        return stringBuilder.toString();
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + resolveTypeName(type) +
                ", desc='" + desc + '\'' +
                '}';
    }
}
