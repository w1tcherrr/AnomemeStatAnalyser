package data.entities;

public record SinglePlayerStats(String name,
                                int totalMapsPlayed,
                                int kills,
                                int headshotKills,
                                int deaths,
                                int assists,
                                double kdr,
                                int totalRoundsPlayed,
                                double adr,
                                double kast,
                                double hltvRating,
                                int triples,
                                int quads,
                                int aces,
                                int mvps,
                                int tks) {

}