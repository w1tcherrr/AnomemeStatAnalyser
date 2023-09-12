package data.entities;

import data.DataContainer;
import lombok.Data;
import utils.DataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class SingleTeamStats {

    public static final String TOTAL = "total";

    private final List<TeamMapStatistic> teamMapStatistics;
    private final List<PairStatistic> pairStatistics;
    private final int totalMapsPlayed, totalRoundsPlayed, totalRoundsWon, totalTRounds, totalTRoundsWon, totalCTRounds, totalCTRoundsWon, totalOvertimeRounds;
    private final double totalTWinPercentage, totalCTWinPercentage, totalWinPercentage;

    private final Map<String, Integer> mapsPlayed, roundsPlayed, roundsWon, tRounds, tRoundsWon, ctRounds, ctRoundsWon, overtimeRounds;
    private final Map<String, Double> tWinPercentage, ctWinPercentage, winPercentage;

    private record PairStatistic(TeamMapStatistic team, TeamMapStatistic enemy) {

    }

    public SingleTeamStats(DataContainer dataContainer, String teamName) {
        pairStatistics = dataContainer.getMapStatistics().stream().map(v -> {
            if (v.getTeamAMapStatistic().getTeamName().equals(teamName)) {
                return new PairStatistic(v.getTeamAMapStatistic(), v.getTeamBMapStatistic());
            }
            if (v.getTeamBMapStatistic().getTeamName().equals(teamName)) {
                return new PairStatistic(v.getTeamBMapStatistic(), v.getTeamAMapStatistic());
            }
            return null;
        }).filter(Objects::nonNull).toList();

        teamMapStatistics = pairStatistics.stream().map(PairStatistic::team).toList();

        totalMapsPlayed = pairStatistics.size();
        totalRoundsPlayed = getRounds(null, true, true, true, true, true, true);
        totalTRounds = getRounds(null, true, false, false, true, false, false);
        totalCTRounds = getRounds(null, false, true, true, false, false, false);
        totalTRoundsWon = getRounds(null, true, false, false, false, false, false);
        totalCTRoundsWon = getRounds(null, true, false, false, false, false, false);
        totalRoundsWon = getRounds(null, true, true, false, false, true, false);
        totalOvertimeRounds = getRounds(null, false, false, false, false, true, true);
        totalTWinPercentage = (double) totalTRoundsWon / totalTRounds * 100;
        totalCTWinPercentage = (double) totalCTRoundsWon / totalCTRounds * 100;
        totalWinPercentage = (double) totalRoundsWon / totalRoundsPlayed * 100;

        mapsPlayed = new HashMap<>();
        roundsPlayed = new HashMap<>();
        roundsWon = new HashMap<>();
        tRounds = new HashMap<>();
        tRoundsWon = new HashMap<>();
        ctRounds = new HashMap<>();
        ctRoundsWon = new HashMap<>();
        tWinPercentage = new HashMap<>();
        ctWinPercentage = new HashMap<>();
        winPercentage = new HashMap<>();
        overtimeRounds = new HashMap<>();

        for (String map : DataUtils.getMaps(dataContainer)) {
            int mapsPlayed1 = (int) pairStatistics.stream().filter(v -> v.team().getMapStatistic().getMapName().equals(map)).count();
            if (mapsPlayed1 != 0) {
                int roundsPlayed1 = getRounds(map, true, true, true, true, true, true);
                int tRounds1 = getRounds(map, true, false, false, true, false, false);
                int ctRounds1 = getRounds(map, false, true, true, false, false, false);
                int tRoundsWon1 = getRounds(map, true, false, false, false, false, false);
                int ctRoundsWon1 = getRounds(map, false, true, false, false, false, false);
                int roundsWon1 = getRounds(map, true, true, false, false, true, false);
                int overtimeRounds1 = getRounds(map, false, false, false, false, true, true);
                double tWinPercentage1 = (double) tRoundsWon1 / tRounds1 * 100;
                double ctWinPercentage1 = (double) ctRoundsWon1 / ctRounds1 * 100;
                double winPercentage1 = (double) roundsWon1 / roundsPlayed1 * 100;

                mapsPlayed.put(map, mapsPlayed1);
                roundsPlayed.put(map, roundsPlayed1);
                roundsWon.put(map, roundsWon1);
                tRounds.put(map, tRounds1);
                tRoundsWon.put(map, tRoundsWon1);
                ctRounds.put(map, ctRounds1);
                ctRoundsWon.put(map, ctRoundsWon1);
                tWinPercentage.put(map, tWinPercentage1);
                ctWinPercentage.put(map, ctWinPercentage1);
                winPercentage.put(map, winPercentage1);
                overtimeRounds.put(map, overtimeRounds1);
            }
        }
    }

    private int getRounds(String map, boolean teamT, boolean teamCT, boolean enemyT, boolean enemyCT, boolean teamOT, boolean enemyOT) {
        List<PairStatistic> currentPairs = pairStatistics;
        if (map != null) {
            currentPairs = currentPairs.stream().filter(pair -> pair.team().getMapStatistic().getMapName().equals(map)).toList();
        }
        int rounds = 0;
        for (PairStatistic current : currentPairs) {
            TeamMapStatistic team = current.team();
            TeamMapStatistic enemy = current.enemy();
            if (teamT) {
                rounds += team.getScoreT();
            }
            if (teamCT) {
                rounds += team.getScoreCT();
            }
            if (enemyT) {
                rounds += enemy.getScoreT();
            }
            if (enemyCT) {
                rounds += enemy.getScoreCT();
            }
            if (teamOT) {
                rounds += (team.getScore()) - (team.getScoreT() + team.getScoreCT());
            }
            if (enemyOT) {
                rounds += (enemy.getScore()) - (enemy.getScoreT() + enemy.getScoreCT());
            }
        }
        return rounds;
    }
}
