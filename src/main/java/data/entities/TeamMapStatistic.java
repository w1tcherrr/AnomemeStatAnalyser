package data.entities;

import lombok.Data;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamMapStatistic {

    @ToString.Exclude
    private final MapStatistic mapStatistic;

    private final String teamName;
    private int score, scoreT, scoreCT;

    private List<PlayerMapStatistic> playerMapStatistics;

    public TeamMapStatistic(JSONObject jsonObject, boolean startCT, MapStatistic mapStatistic) {
        this.mapStatistic = mapStatistic;

        JSONObject teamObject = startCT ? jsonObject.getJSONObject("team_ct") : jsonObject.getJSONObject("team_t");

        teamName = teamObject.getString("team_name").replace("[READY] ", "");
        score = teamObject.getInt("score");

        scoreT = startCT ? teamObject.getInt("score_second_half") : teamObject.getInt("score_first_half");
        scoreCT = startCT ? teamObject.getInt("score_first_half") : teamObject.getInt("score_second_half");

        playerMapStatistics = new ArrayList<>();

        JSONArray arr = teamObject.getJSONArray("team_players");
        for (Object object : arr) {
            if (object instanceof JSONObject playerObject) {
                PlayerMapStatistic playerMapStatistic = new PlayerMapStatistic();
                playerMapStatistic.setPlayerName(playerObject.getString("name"));
                playerMapStatistic.setKills(playerObject.getInt("kill_count"));
                playerMapStatistic.setKillsWithHS(playerObject.getInt("hs_count"));
                playerMapStatistic.setDeaths(playerObject.getInt("death_count"));
                playerMapStatistic.setAssists(playerObject.getInt("assist_count"));
                playerMapStatistic.setTotalRoundsPlayed(jsonObject.getJSONObject("team_ct").getInt("score") + jsonObject.getJSONObject("team_t").getInt("score"));
                playerMapStatistic.setKast(playerObject.getDouble("kast"));
                playerMapStatistic.setTriples(playerObject.getInt("3k_count"));
                playerMapStatistic.setQuads(playerObject.getInt("4k_count"));
                playerMapStatistic.setAces(playerObject.getInt("5k_count"));
                playerMapStatistic.setMvps(playerObject.getInt("mvp_count"));
                playerMapStatistic.setTks(playerObject.getInt("tk_count"));
                playerMapStatistic.setTeamName(teamName);
                double adr = 0;
                String id = playerObject.getString("steamid");
                for (Object obj : playerObject.getJSONArray("players_hurted")) {
                    if (obj instanceof JSONObject damageObj) {
                        if (damageObj.getString("attacker_steamid").equals(id)) {
                            adr += damageObj.getInt("health_damage");
                        }
                    }
                }
                playerMapStatistic.setTeamMapStatistic(this);
                adr = adr / (jsonObject.getJSONObject("team_ct").getInt("score") + jsonObject.getJSONObject("team_t").getInt("score"));
                playerMapStatistic.setAdr(adr);
                playerMapStatistic.setHltvRating(playerObject.getDouble("hltv2_rating"));
                playerMapStatistics.add(playerMapStatistic);
            }
        }

        // make immutable
        playerMapStatistics = List.copyOf(playerMapStatistics);
    }
}
