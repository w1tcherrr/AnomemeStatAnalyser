package utils;

import data.DataContainer;
import data.entities.*;

import java.util.*;
import java.util.stream.Collectors;

public class DataFormatter {

    private static final DataUtils.StatsProvider provider = DataUtils.StatsProvider.OVER_OR_5_MATCHES;

    public static List<String> createStatsSortedByKD(DataContainer dataContainer) {
        return DataUtils.formatForSortedPlayerStats(DataUtils.getAllSinglePlayerStats(dataContainer), "K/D", new DataUtils.ValueProvider<SinglePlayerStats, Double>() {
            @Override
            public Double getValue(SinglePlayerStats stats) {
                return (double) stats.kills() / stats.deaths();
            }

            @Override
            public boolean reversed() {
                return true;
            }

        }, provider);
    }

    public static List<String> createStatsSortedByKPR(DataContainer dataContainer) {
        return DataUtils.formatForSortedPlayerStats(DataUtils.getAllSinglePlayerStats(dataContainer), "KPR", new DataUtils.ValueProvider<SinglePlayerStats, Double>() {
            @Override
            public Double getValue(SinglePlayerStats stats) {
                return (double) stats.kills() / stats.totalRoundsPlayed();
            }

            @Override
            public boolean reversed() {
                return true;
            }

        }, DataUtils.StatsProvider.OVER_OR_5_MATCHES);
    }

    public static List<String> createStatsSortedByADR(DataContainer dataContainer) {
        return DataUtils.formatForSortedPlayerStats(DataUtils.getAllSinglePlayerStats(dataContainer), "ADR", new DataUtils.ValueProvider<SinglePlayerStats, Double>() {
            @Override
            public Double getValue(SinglePlayerStats stats) {
                return stats.adr();
            }

            @Override
            public boolean reversed() {
                return true;
            }

        }, DataUtils.StatsProvider.OVER_OR_5_MATCHES);
    }

    public static List<String> createStatsSortedByKAST(DataContainer dataContainer) {
        return DataUtils.formatForSortedPlayerStats(DataUtils.getAllSinglePlayerStats(dataContainer), "KAST", new DataUtils.ValueProvider<SinglePlayerStats, Double>() {
            @Override
            public Double getValue(SinglePlayerStats stats) {
                return stats.kast();
            }

            @Override
            public boolean reversed() {
                return true;
            }

        }, DataUtils.StatsProvider.OVER_OR_5_MATCHES);
    }

    public static List<String> createStatsSortedByHLTV(DataContainer dataContainer) {
        return DataUtils.formatForSortedPlayerStats(DataUtils.getAllSinglePlayerStats(dataContainer), "HLTV", new DataUtils.ValueProvider<SinglePlayerStats, Double>() {
            @Override
            public Double getValue(SinglePlayerStats stats) {
                return stats.hltvRating();
            }

            @Override
            public boolean reversed() {
                return true;
            }

        }, DataUtils.StatsProvider.OVER_OR_5_MATCHES);
    }

    public static List<String> createStatsSortedByHeadshotPercent(DataContainer dataContainer) {
        return DataUtils.formatForSortedPlayerStats(DataUtils.getAllSinglePlayerStats(dataContainer), "HS%%", new DataUtils.ValueProvider<SinglePlayerStats, Double>() {
            @Override
            public Double getValue(SinglePlayerStats stats) {
                return (double) stats.headshotKills() / stats.kills() * 100;
            }

            @Override
            public boolean reversed() {
                return true;
            }

        }, DataUtils.StatsProvider.OVER_OR_5_MATCHES);
    }

    public static List<String> createTeamStats(DataContainer dataContainer) {
        List<String> lines = new ArrayList<>();

        List<SinglePlayerStats> fullStats = DataUtils.getAllSinglePlayerStats(dataContainer, 0);

        String headerFormat = "%-20s   MAPS  HLTV  K/A/D        +/-     ADR   KAST    HS%%  3k  4k  5k MVP  TK";

        List<String> teamNames = DataUtils.getTeamNames(dataContainer);

        for (String teamName : teamNames) {
            List<String> teamPlayers = DataUtils.getPlayerNamesForTeam(dataContainer, teamName);

            List<SinglePlayerStats> allPlayerStats = fullStats.stream().filter(s -> teamPlayers.contains(s.name())).toList();

            SingleTeamStats singleTeamStats = DataUtils.getTeamStats(dataContainer, teamName);

            lines.add(String.format(headerFormat, teamName));
            for (SinglePlayerStats playerStats : allPlayerStats.stream().sorted(Comparator.comparingDouble(v -> -v.hltvRating())).toList()) {
                lines.add(DataUtils.formatFullStats(playerStats));
            }

            lines.add(String.format("Rounds, total: %2.2f%% - %d/%d, T:  %2.2f%% - %d/%d, CT:  %2.2f%% - %d/%d",
                    singleTeamStats.getTotalWinPercentage(), singleTeamStats.getTotalRoundsWon(), singleTeamStats.getTotalRoundsPlayed(),
                    singleTeamStats.getTotalTWinPercentage(), singleTeamStats.getTotalTRoundsWon(), singleTeamStats.getTotalTRounds(),
                    singleTeamStats.getTotalCTWinPercentage(), singleTeamStats.getTotalCTRoundsWon(), singleTeamStats.getTotalCTRounds()
            ));

            lines.add("");
            lines.add("");
        }

        return lines;
    }

    public static List<String> createTeamMapStats(DataContainer dataContainer) {
        List<String> lines = new ArrayList<>();

        List<String> teamNames = DataUtils.getTeamNames(dataContainer);

        for (String teamName : teamNames) {
            SingleTeamStats singleTeamStats = DataUtils.getTeamStats(dataContainer, teamName);

            lines.add(teamName);

            for (String map : singleTeamStats.getMapsPlayed().entrySet().stream().sorted(Comparator.comparingInt(v -> -v.getValue())).map(Map.Entry::getKey).toList()) {
                lines.add(String.format("%s: played %d times, rounds total: %2.2f%% - %d/%d, T:  %2.2f%% - %d/%d, CT:  %2.2f%% - %d/%d",
                        map, singleTeamStats.getMapsPlayed().get(map),
                        singleTeamStats.getWinPercentage().get(map), singleTeamStats.getRoundsWon().get(map), singleTeamStats.getRoundsPlayed().get(map),
                        singleTeamStats.getTWinPercentage().get(map), singleTeamStats.getTRoundsWon().get(map), singleTeamStats.getTRounds().get(map),
                        singleTeamStats.getCtWinPercentage().get(map), singleTeamStats.getCtRoundsWon().get(map), singleTeamStats.getCtRounds().get(map)
                ));
            }

            lines.add("");
            lines.add("");
        }

        return lines;
    }

    public static List<String> createSingleMapStats(DataContainer dataContainer) {
        List<String> lines = new ArrayList<>();
        for (MapStatistic mapStatistic : dataContainer.getMapStatistics()) {
            TeamMapStatistic teamAMapStatistic = mapStatistic.getTeamAMapStatistic();
            TeamMapStatistic teamBMapStatistic = mapStatistic.getTeamBMapStatistic();

            if (teamAMapStatistic.getScore() < teamBMapStatistic.getScore()) {
                TeamMapStatistic temp = teamAMapStatistic;
                teamAMapStatistic = teamBMapStatistic;
                teamBMapStatistic = temp;
            }

            String fullHeaderFormat = "%s vs %s on %s with Score %-2d:%-2d";
            String teamHeaderFormat = "%-20s HLTV       K/A/D   +/-     ADR   KAST    HS%%  3k  4k  5k MVP  TK";

            lines.add(String.format(fullHeaderFormat, teamAMapStatistic.getTeamName(), teamBMapStatistic.getTeamName(), mapStatistic.getMapName(), teamAMapStatistic.getScore(), teamBMapStatistic.getScore()));
            lines.add(String.format(teamHeaderFormat, teamAMapStatistic.getTeamName()));
            for (PlayerMapStatistic playerStats : teamAMapStatistic.getPlayerMapStatistics().stream().sorted(Comparator.comparingDouble(st -> -st.getHltvRating())).toList()) {
                lines.add(DataUtils.formatFullStats(playerStats));
            }
            lines.add("");
            lines.add(String.format(teamHeaderFormat, teamBMapStatistic.getTeamName()));
            for (PlayerMapStatistic playerStats : teamBMapStatistic.getPlayerMapStatistics().stream().sorted(Comparator.comparingDouble(st -> -st.getHltvRating())).toList()) {
                lines.add(DataUtils.formatFullStats(playerStats));
            }

            lines.add("");
            lines.add("");
        }

        return lines;
    }


    public static List<String> createStatsSortedByHLTVRatingInSingleMatch(DataContainer dataContainer) {
        List<String> lines = new ArrayList<>();
        List<PlayerMapStatistic> sorted = dataContainer.getMapStatistics().stream().map(mapSt -> {
            List<PlayerMapStatistic> allPlayerMapStatistics = new ArrayList<>();
            allPlayerMapStatistics.addAll(mapSt.getTeamAMapStatistic().getPlayerMapStatistics());
            allPlayerMapStatistics.addAll(mapSt.getTeamBMapStatistic().getPlayerMapStatistics());
            return allPlayerMapStatistics;
        }).flatMap(Collection::stream).sorted(Comparator.comparingDouble(playerSt -> -playerSt.getHltvRating())).toList();

        for (int i = 0; i < sorted.size(); i++) {
            PlayerMapStatistic playerMapStatistic = sorted.get(i);
            TeamMapStatistic playerTeamMapStatistic = playerMapStatistic.getTeamMapStatistic();
            TeamMapStatistic enemyTeamMapStatistic = playerMapStatistic.getTeamMapStatistic().getMapStatistic().getTeamAMapStatistic() == playerTeamMapStatistic ?
                    playerMapStatistic.getTeamMapStatistic().getMapStatistic().getTeamBMapStatistic() :
                    playerMapStatistic.getTeamMapStatistic().getMapStatistic().getTeamAMapStatistic();
            String playerName = playerMapStatistic.getPlayerName();
            String enemyTeamName = enemyTeamMapStatistic.getTeamName();

            lines.add(String.format("%-2d %-15s vs %-20s SCORE: %d-%d MAP: %s", i + 1, playerName, enemyTeamName, playerTeamMapStatistic.getScore(), enemyTeamMapStatistic.getScore(), playerTeamMapStatistic.getMapStatistic().getMapName()));
            lines.add(String.format("HLTV: %-2.2f   KDR: %2dK %2dA %2dD", playerMapStatistic.getHltvRating(), playerMapStatistic.getKills(), playerMapStatistic.getAssists(), playerMapStatistic.getDeaths()));
            lines.add("");
        }

        return lines;
    }

    public static List<String> createMapsSortedByAmountPlayed(DataContainer dataContainer) {
        List<String> lines = new ArrayList<>();
        dataContainer.getMapStatistics().stream().map(MapStatistic::getMapName)
                .collect(Collectors.groupingBy(s -> s))
                .entrySet().stream()
                .sorted(Comparator.comparingInt(i -> -i.getValue().size()))
                .forEach(e -> lines.add(e.getKey() + " - " + e.getValue().size()));
        return lines;
    }
}
