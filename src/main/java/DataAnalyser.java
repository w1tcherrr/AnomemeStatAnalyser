import data.DataContainer;
import org.json.JSONException;
import org.json.JSONObject;
import utils.DataFormatter;
import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DataAnalyser {

    private final String outputDirectory;

    private final DataContainer dataContainer;

    public DataAnalyser(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.dataContainer = new DataContainer();
    }

    public void run() throws IOException, JSONException {

        List<Path> jsonFiles = Utils.findFilesInDirectory(outputDirectory, ".json");

        for (Path path : jsonFiles) {
            File file = path.toFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String jsonString = br.lines().collect(Collectors.joining("\r\n"));
            JSONObject obj = new JSONObject(jsonString);
            dataContainer.add(obj);
        }

        Utils.writeFile(outputDirectory, "EXPORT", "kd.txt", DataFormatter.createStatsSortedByKD(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "kpr.txt", DataFormatter.createStatsSortedByKPR(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "hltv.txt", DataFormatter.createStatsSortedByHLTV(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "adr.txt", DataFormatter.createStatsSortedByADR(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "kast.txt", DataFormatter.createStatsSortedByKAST(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "hs.txt", DataFormatter.createStatsSortedByHeadshotPercent(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "hltv_per_match_desc.txt", DataFormatter.createStatsSortedByHLTVRatingInSingleMatch(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "maps_played_desc.txt", DataFormatter.createMapsSortedByAmountPlayed(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "team_stats.txt", DataFormatter.createTeamStats(dataContainer));
        Utils.writeFile(outputDirectory, "EXPORT", "map_stats.txt", DataFormatter.createSingleMapStats(dataContainer));
    }
}
