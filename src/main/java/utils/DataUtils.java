package utils;

import data.DataContainer;
import data.entities.PlayerMapStatistic;
import data.entities.SinglePlayerStats;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DataUtils {

    public static SinglePlayerStats getStatsForPlayer(String playerName, DataContainer dataContainer) {
        if (playerName.startsWith("(1)")) return null;
        List<PlayerMapStatistic> playerMapStatistics = dataContainer.getMapStatistics().stream().map(mapStatistic -> {
            AtomicReference<PlayerMapStatistic> playerMapStatistic = new AtomicReference<>();
            mapStatistic.getTeamAMapStatistic().getPlayerMapStatistics().stream().filter(playerMapStatistic1 -> playerMapStatistic1.getPlayerName().equals(playerName) || playerMapStatistic1.getPlayerName().equals("(1)" + playerName)).findFirst().ifPresent(playerMapStatistic::set);
            mapStatistic.getTeamBMapStatistic().getPlayerMapStatistics().stream().filter(playerMapStatistic1 -> playerMapStatistic1.getPlayerName().equals(playerName) || playerMapStatistic1.getPlayerName().equals("(1)" + playerName)).findFirst().ifPresent(playerMapStatistic::set);
            return playerMapStatistic.get();
        }).filter(Objects::nonNull).toList();
        return new SinglePlayerStats(playerName,
                playerMapStatistics.size(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getKills).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getKillsWithHS).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getDeaths).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getAssists).sum(),
                (double) playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getKills).sum() / playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getDeaths).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getTotalRoundsPlayed).sum(),
                calculateAdrPerRound(playerMapStatistics),
                calculateKastPerRound(playerMapStatistics),
                calculateHltvRatingPerRound(playerMapStatistics),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getTriples).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getQuads).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getAces).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getMvps).sum(),
                playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getTks).sum());
    }

    public static List<SinglePlayerStats> getAllSinglePlayerStats(DataContainer dataContainer) {
        return getAllSinglePlayerStats(dataContainer, 0);
    }

    public static List<SinglePlayerStats> getAllSinglePlayerStats(DataContainer dataContainer, int minMaps) {
        List<String> allNames = dataContainer.getMapStatistics().stream().map(mapStatistic -> {
            List<String> a = mapStatistic.getTeamAMapStatistic().getPlayerMapStatistics().stream().map(PlayerMapStatistic::getPlayerName).collect(Collectors.toList());
            a.addAll(mapStatistic.getTeamBMapStatistic().getPlayerMapStatistics().stream().map(PlayerMapStatistic::getPlayerName).toList());
            return a;
        }).flatMap(Collection::stream).distinct().toList();

        List<SinglePlayerStats> stats = new ArrayList<>();

        for (String name : allNames) {
            var stats1 = getStatsForPlayer(name, dataContainer);
            if (stats1 != null && stats1.totalMapsPlayed() >= minMaps) stats.add(stats1);
        }

        return stats;
    }

    private static double calculateAdrPerRound(List<PlayerMapStatistic> playerMapStatistics) {
        int totalRounds = playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getTotalRoundsPlayed).sum();
        double totalDamage = playerMapStatistics.stream().mapToDouble(st -> st.getAdr() * st.getTotalRoundsPlayed()).sum();
        return totalDamage / totalRounds;
    }

    private static double calculateKastPerRound(List<PlayerMapStatistic> playerMapStatistics) {
        int totalRounds = playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getTotalRoundsPlayed).sum();
        double totalKast = playerMapStatistics.stream().mapToDouble(st -> st.getKast() * st.getTotalRoundsPlayed()).sum();
        return totalKast / totalRounds;
    }

    private static double calculateHltvRatingPerRound(List<PlayerMapStatistic> playerMapStatistics) {
        int totalRounds = playerMapStatistics.stream().mapToInt(PlayerMapStatistic::getTotalRoundsPlayed).sum();
        double totalRating = playerMapStatistics.stream().mapToDouble(st -> st.getHltvRating() * st.getTotalRoundsPlayed()).sum();
        return totalRating / totalRounds;
    }

    public static List<String> getPlayerNamesForTeam(DataContainer dataContainer, String teamName) {
        return dataContainer.getMapStatistics().stream().map(map -> {
            List<String> players = new ArrayList<>();
            if (map.getTeamAMapStatistic().getTeamName().equals(teamName)) {
                players.addAll(map.getTeamAMapStatistic().getPlayerMapStatistics().stream().map(PlayerMapStatistic::getPlayerName).toList());
            }
            if (map.getTeamBMapStatistic().getTeamName().equals(teamName)) {
                players.addAll(map.getTeamBMapStatistic().getPlayerMapStatistics().stream().map(PlayerMapStatistic::getPlayerName).toList());
            }
            return players;
        }).flatMap(Collection::stream).toList();
    }


    public static String formatFullStats(PlayerMapStatistic playerStats) {
        return formatFullStats(0, false, playerStats.getPlayerName(), playerStats.getKills(), playerStats.getAssists(), playerStats.getDeaths(),
                playerStats.getAdr(), playerStats.getKast(), playerStats.getKillsWithHS(), playerStats.getHltvRating(),
                playerStats.getTriples(), playerStats.getQuads(), playerStats.getAces(), playerStats.getMvps(), playerStats.getTks());
    }

    public static String formatFullStats(SinglePlayerStats playerStats) {
        return formatFullStats(playerStats.totalMapsPlayed(), true, playerStats.name(), playerStats.kills(), playerStats.assists(), playerStats.deaths(),
                playerStats.adr(), playerStats.kast(), playerStats.headshotKills(), playerStats.hltvRating(),
                playerStats.triples(), playerStats.quads(), playerStats.aces(), playerStats.mvps(), playerStats.tks());
    }

    private static String formatFullStats(int totalMaps, boolean printTotalMaps, String playerName, int kills, int assists, int deaths, double adr, double kast, int headshotKills, double hltv, int triples, int quads, int aces, int mvps, int tks) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.rightPad(playerName, 20));
        sb.append(" ");
        if (printTotalMaps) {
            sb.append("    ");
            sb.append(StringUtils.leftPad(totalMaps, 2));
            sb.append("  ");
        }
        sb.append(String.format("%-1.2f", hltv));
        sb.append("  ");
        sb.append(StringUtils.rightPad(kills, 3));
        sb.append("/");
        sb.append(StringUtils.rightPad(assists, 3));
        sb.append("/");
        sb.append(StringUtils.rightPad(deaths, 3));
        sb.append(" ");
        sb.append(StringUtils.formatPlusMinus(kills, deaths, 4));
        sb.append("  ");
        sb.append(StringUtils.leftPad(String.format("%-2.2f", adr), 6));
        sb.append("  ");
        sb.append(StringUtils.leftPad(String.format("%-2.2f", kast), 4));
        sb.append("  ");
        sb.append(StringUtils.leftPad(StringUtils.formatHeadshotPercent(headshotKills, kills), 5));
        sb.append("  ");
        sb.append(StringUtils.leftPad(triples, 2));
        sb.append("  ");
        sb.append(StringUtils.leftPad(quads, 2));
        sb.append("  ");
        sb.append(StringUtils.leftPad(aces, 2));
        sb.append("  ");
        sb.append(StringUtils.leftPad(mvps, 2));
        sb.append("   ");
        sb.append(tks);
        return sb.toString();
    }

    abstract static class ValueProvider<T, U> {
        abstract public U getValue(T t);

        public boolean reversed() {
            return false;
        }
    }

    abstract static class StatsProvider {
        public static StatsProvider OVER_OR_5_MATCHES = new StatsProvider() {
            @Override
            public boolean allowRow(SinglePlayerStats stats) {
                return stats.totalMapsPlayed() >= 5;
            }
        };

        abstract public boolean allowRow(SinglePlayerStats stats);
    }

    public static List<String> formatForSortedPlayerStats(List<SinglePlayerStats> allStats, String statName, ValueProvider<SinglePlayerStats, ?> valueProvider, StatsProvider statsProvider) {
        int c = 1;
        List<String> lines = new ArrayList<>();
        List<SinglePlayerStats> sortedStats = allStats.stream().sorted(getComparator(valueProvider, allStats.get(0))).toList();
        for (SinglePlayerStats stats : sortedStats) {
            if (!statsProvider.allowRow(stats)) {
                continue;
            }
            String formatString = "%-3d %-20s %s: " + getFormatForClass(valueProvider, allStats.get(0));
            lines.add(String.format(formatString, c++, stats.name(), statName, valueProvider.getValue(stats)));
        }
        return lines;
    }

    private static Comparator<? super SinglePlayerStats> getComparator(ValueProvider<SinglePlayerStats, ?> valueProvider, SinglePlayerStats stat) {
        Object value = valueProvider.getValue(stat);
        if (value.getClass() == Double.class) {
            if (valueProvider.reversed()) {
                return Comparator.comparingDouble(v -> -(double) valueProvider.getValue(v));
            } else {
                return Comparator.comparingDouble(v -> (double) valueProvider.getValue(v));
            }
        }
        throw new IllegalStateException();
    }

    private static String getFormatForClass(ValueProvider<SinglePlayerStats, ?> valueProvider, SinglePlayerStats stat) {
        Object value = valueProvider.getValue(stat);
        if (value.getClass() == Double.class) {
            return "%2.3f";
        }
        throw new IllegalStateException();
    }
}
