package utils;

import data.entities.TeamMapStatistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Utils {
    public static List<Path> findFilesInDirectory(String path, String fileEnding) {
        try (Stream<Path> files = Files.find(Paths.get(path), 999, (p, bfa) -> bfa.isRegularFile())) {
            return files.filter(f -> f.getFileName().toString().endsWith(fileEnding)).toList();
        } catch (IOException e) {
            if (!(e instanceof NoSuchFileException)) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
        return List.of();
    }

    public static void runCommandForDemoFile(Path path, String outputDirectory) {
        String command = "csgodm json \"" + path.toAbsolutePath() + "\" --force-analyze --source faceit --output \"" + outputDirectory + "\"";
        try {
            Process process = Runtime.getRuntime().exec(command);
            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String path, String directory, String fileName, List<String> lines) throws IOException {
        File file = new File(path);
        file.mkdirs();
        if (!file.exists()) throw new IllegalStateException();
        directory = file + "\\" + directory;
        File directoryFile = new File(directory);
        if ((directoryFile.exists() && directoryFile.isDirectory()) || directoryFile.mkdir()) {
            String fullFilePath = directory + "\\" + fileName;
            File outputFile = new File(fullFilePath);
            if ((outputFile.exists() && outputFile.isFile()) || outputFile.createNewFile()) {
                try (BufferedWriter br = new BufferedWriter(new FileWriter(outputFile))) {
                    for (String line : lines) {
                        br.write(line);
                        br.newLine();
                    }
                }
            }
        }
    }

    public static void fixScore(TeamMapStatistic teamOneMapStatistic, TeamMapStatistic teamTwoMapStatistic) {
        TeamMapStatistic teamAMapStatistic;
        TeamMapStatistic teamBMapStatistic;
        teamAMapStatistic = teamOneMapStatistic.getScore() > teamTwoMapStatistic.getScore() ? teamOneMapStatistic : teamTwoMapStatistic;
        teamBMapStatistic = teamOneMapStatistic.getScore() > teamTwoMapStatistic.getScore() ? teamTwoMapStatistic : teamOneMapStatistic;

        boolean aStartedAsCT = teamOneMapStatistic.getScore() > teamTwoMapStatistic.getScore();

        if (isNormalScore(teamAMapStatistic, teamBMapStatistic)) {
            return;
        }

        boolean teamAScoreTooLittle = teamAMapStatistic.getScore() == 15 || (teamAMapStatistic.getScore() - 16) % 3 == 2;
        teamBMapStatistic.setScore(teamAScoreTooLittle ? teamBMapStatistic.getScore() - 1 : teamBMapStatistic.getScore() + 1);
        teamAMapStatistic.setScore(teamAScoreTooLittle ? teamAMapStatistic.getScore() + 1 : teamAMapStatistic.getScore() - 1);

        if (aStartedAsCT && teamAScoreTooLittle) {
            teamAMapStatistic.setScoreCT(teamAMapStatistic.getScoreCT() + 1);
            teamBMapStatistic.setScoreT(teamBMapStatistic.getScoreT() - 1);
        } else if (aStartedAsCT) { // && !teamAScoreTooLittle
            teamAMapStatistic.setScoreCT(teamAMapStatistic.getScoreCT() - 1);
            teamBMapStatistic.setScoreT(teamBMapStatistic.getScoreT() + 1);
        } else if (teamAScoreTooLittle) { // && !aStartedAsCT &&
            teamAMapStatistic.setScoreT(teamAMapStatistic.getScoreT() + 1);
            teamBMapStatistic.setScoreCT(teamAMapStatistic.getScoreCT() - 1);
        } else { // !aStartedAsCT && !teamAScoreTooLittle
            teamAMapStatistic.setScoreT(teamAMapStatistic.getScoreT() - 1);
            teamBMapStatistic.setScoreCT(teamBMapStatistic.getScoreCT() + 1);
        }
    }

    private static boolean isNormalScore(TeamMapStatistic teamAMapStatistic, TeamMapStatistic teamBMapStatistic) {
        if (teamAMapStatistic.getScore() == 16 && teamBMapStatistic.getScore() <= 14) {
            return true;
        }
        if (teamAMapStatistic.getScore() == 15 || teamAMapStatistic.getScore() == 17) {
            return false;
        }
        // Overtime
        int score = teamAMapStatistic.getScore();
        return (score - 16) % 3 == 0;
    }

    public static String simplifyMapName(String mapName) {
        switch (mapName) {
            case "de_tuscan", "workshop/2458920550/de_tuscan" -> {
                return "Tuscan";
            }
            case "workshop/1466175580/de_cbble" -> {
                return "Cobble";
            }
            case "workshop/2550638285/de_rampart_final" -> {
                return "Rampart";
            }
            case "workshop/1984227885/de_feast" -> {
                return "Feast";
            }
            case "workshop/401145257/de_fire" -> {
                return "Fire";
            }
            case "workshop/1318698056/de_subzero" -> {
                return "Subzero";
            }
            case "workshop/727934907/de_thrill" -> {
                return "Thrill";
            }
        }
        return mapName;
    }
}
