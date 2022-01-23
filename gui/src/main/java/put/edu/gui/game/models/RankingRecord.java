package put.edu.gui.game.models;

public class RankingRecord {
    private final String nick;
    private final int pos;
    private final int score;

    public RankingRecord(String nick, int pos, int score) {
        this.nick = nick;
        this.pos = pos;
        this.score = score;
    }

    public String getNick() {
        return nick;
    }

    public int getScore() {
        return score;
    }

    public int getPos() {
        return pos;
    }
}
