package data.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlayerMapStatistic {
    private String playerName, teamName;
    private int kills, killsWithHS, deaths, assists, totalRoundsPlayed, triples, quads, aces, mvps, tks;
    private double adr, kast, hltvRating;

    @ToString.Exclude
    private TeamMapStatistic teamMapStatistic;
}
