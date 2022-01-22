package put.edu.gui.game.models;

public class RankingRecord {
    private final String nick;
    private final int score;

    public RankingRecord(String nick, int score) {
        this.nick = nick;
        this.score = score;
    }

    public String getNick() {
        return nick;
    }

    public int getScore() {
        return score;
    }
}
