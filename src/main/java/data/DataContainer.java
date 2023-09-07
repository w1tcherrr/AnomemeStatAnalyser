package data;

import data.entities.MapStatistic;
import lombok.Data;
import lombok.ToString;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ToString
@Data
public class DataContainer {
    private final List<MapStatistic> mapStatistics = new ArrayList<>();

    public void add(JSONObject obj) {
        MapStatistic mapStatistic = new MapStatistic(obj);
        mapStatistics.add(mapStatistic);
    }
}
