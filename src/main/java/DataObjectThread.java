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

  private DataKind type;
  private List<String> data;

  private static final DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
  private static final DateTimeFormatter formatWeek = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter formatMonth = DateTimeFormatter.ofPattern("yyyy-MM");

  private static final Map<DataKind, DateTimeFormatter> formatters = new HashMap<DataKind, DateTimeFormatter>() {{
    put(DataKind.HOUR, formatHour);
    put(DataKind.DAY, formatWeek);
    put(DataKind.MONTH, formatMonth);
  }};

  DataObjectThread(DataKind type, List<String> listData) {
    this.type = type;
    this.data = listData;
  }

  public JsonObject call() {
    Map<String, Integer> dataMap = new TreeMap<>();
    for (String singleData : this.data) {
      if (!type.equals(DataKind.DEVICE_ID)) {
        LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(singleData)),
                TimeZone.getDefault().toZoneId());
        singleData = d.format(formatters.get(type));
      }

      Integer nrElements = dataMap.get(singleData);
      nrElements = nrElements == null ?  1 : nrElements + 1;
      dataMap.put(singleData, nrElements);
    }
    
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
      jsonObjectBuilder.add(entry.getKey(), entry.getValue());
    }
    
    return jsonObjectBuilder.build();
  }
}
