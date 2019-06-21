import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class DataObjectThread implements Callable<JsonObject> {

  private DataObject.DataKind type;
  private List<String> data;

  private static final DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
  private static final DateTimeFormatter formatWeek = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter formatMonth = DateTimeFormatter.ofPattern("yyyy-MM");

  private static final Map<DataObject.DataKind, DateTimeFormatter> formatters = new HashMap<DataObject.DataKind, DateTimeFormatter>() {{
    put(DataObject.DataKind.HOUR, formatHour);
    put(DataObject.DataKind.DAY, formatWeek);
    put(DataObject.DataKind.MONTH, formatMonth);
  }};

  DataObjectThread(DataObject.DataKind type, List<String> listData) {
    this.type = type;
    this.data = listData;
  }

  public JsonObject call() {
    Integer nrElements;
    Map<String, Integer> dataMap = new TreeMap<>();
    for (String singleData : this.data) {
      if (!type.equals(DataObject.DataKind.DEVICE_ID)) {
        LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(singleData)),
                TimeZone.getDefault().toZoneId());
        singleData = d.format(formatters.get(type));
      }
      nrElements = dataMap.get(singleData);
      if (nrElements == null) {
        dataMap.put(singleData, 1);
      } else {
        dataMap.put(singleData, nrElements + 1);
      }
    }
    
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
      jsonObjectBuilder.add(entry.getKey(), entry.getValue());
    }
    return jsonObjectBuilder.build();
  }

}
