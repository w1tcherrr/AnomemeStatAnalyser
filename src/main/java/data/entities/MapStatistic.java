package data.entities;

import lombok.Data;
import lombok.ToString;
import org.json.JSONObject;
import utils.Utils;

@Data
@ToString
public class MapStatistic {

    private final TeamMapStatistic teamAMapStatistic;
    private final TeamMapStatistic teamBMapStatistic;
    private final String mapName;

    public MapStatistic(JSONObject jsonObject) {
        TeamMapStatistic teamOneMapStatistic = new TeamMapStatistic(jsonObject, true, this);
        TeamMapStatistic teamTwoMapStatistic = new TeamMapStatistic(jsonObject, false, this);

        teamAMapStatistic = teamOneMapStatistic.getScore() > teamTwoMapStatistic.getScore() ? teamOneMapStatistic : teamTwoMapStatistic;
        teamBMapStatistic = teamOneMapStatistic.getScore() > teamTwoMapStatistic.getScore() ? teamTwoMapStatistic : teamOneMapStatistic;

        mapName = Utils.simplifyMapName(jsonObject.getString("map_name"));

        Utils.fixScore(teamOneMapStatistic, teamTwoMapStatistic);
    }
}
