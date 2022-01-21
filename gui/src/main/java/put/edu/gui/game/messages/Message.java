package put.edu.gui.game.messages;


public abstract class Message {
    private final int type;
    private String desc;

    public Message(int type) {
        this.type = type;
    }

    public Message(int type, String desc) {
        this.type = type;
        this.desc = desc;
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
                "type=" + type +
                ", desc='" + desc + '\'' +
                '}';
    }
}
