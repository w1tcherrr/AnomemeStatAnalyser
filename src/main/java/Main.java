import lombok.SneakyThrows;
import org.json.JSONException;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, JSONException {
        String pathDirectory = "<<DEMOPATH>>";
        String outputDirectory = "<<OUTPUTPATH>>";

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

        deleteFiles(outputDirectory);
    }

    @SneakyThrows
    private static void deleteFiles(String outputDirectory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(outputDirectory))) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isExported(String outputDirectory, Path path) {
        String fileName = path.getFileName().toString().replaceAll(".dem", ".dem.json");
        File file = new File(outputDirectory + "\\" + fileName);
        return Files.exists(file.toPath());
    }
}
