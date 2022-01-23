package put.edu.gui.game.messages.responses;

import put.edu.gui.game.messages.Message;
import put.edu.gui.game.models.RankingRecord;

import java.util.List;
import java.util.Map;

public class EndRoundMessage extends Message {
    private final Map<String, Integer> results;
    private final List<RankingRecord> ranking;

    public EndRoundMessage(int type, Map<String, Integer> results, List<RankingRecord> ranking) {
        super(type);
        this.results = results;
        this.ranking = ranking;
    }

    public Map<String, Integer> getResults() {
        return results;
    }

    public List<RankingRecord> getRanking() {
        return ranking;
    }
}
