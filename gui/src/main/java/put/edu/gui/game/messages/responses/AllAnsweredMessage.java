package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.models.RankingRecord;

import java.util.List;

public class AllAnsweredMessage extends Message {
    private final int A;
    private final int B;
    private final int C;
    private final int D;
    private final List<RankingRecord> ranking;

    public AllAnsweredMessage(int type, int a, int b, int c, int d, List<RankingRecord> ranking) {
        super(type);
        A = a;
        B = b;
        C = c;
        D = d;
        this.ranking = ranking;
    }

    public int getA() {
        return A;
    }

    public int getB() {
        return B;
    }

    public int getC() {
        return C;
    }

    public int getD() {
        return D;
    }

    public List<RankingRecord> getRanking() {
        return ranking;
    }
}
