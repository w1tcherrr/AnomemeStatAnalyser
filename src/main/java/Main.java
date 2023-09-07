import org.json.JSONException;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, JSONException {
        String pathDirectory = "C:\\Users\\Misch\\Downloads\\Demos\\SINGLE\\ACS4\\PLAYOFFS";
        String outputDirectory = "C:\\Users\\Misch\\Downloads\\Demos\\OUTPUT";

        // analyse files
        List<Path> filePaths = Utils.findFilesInDirectory(pathDirectory, ".dem");
        try (ExecutorService exs = Executors.newFixedThreadPool(16)) {
            for (Path path : filePaths) {
                if (isExported(outputDirectory, path)) {
                    System.out.println("ALREADY EXPORTED " + path);
                    continue;
                }
                exs.submit(() -> Utils.runCommandForDemoFile(path, outputDirectory));
            }
        }

        // analyse data
        DataAnalyser dataAnalyser = new DataAnalyser(outputDirectory);
        dataAnalyser.run();
    }

    private static boolean isExported(String outputDirectory, Path path) {
        String fileName = path.getFileName().toString().replaceAll(".dem", ".dem.json");
        File file = new File(outputDirectory + "\\" + fileName);
        return Files.exists(file.toPath());
    }
}