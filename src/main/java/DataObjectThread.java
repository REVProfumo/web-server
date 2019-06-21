import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class DataObjectThread implements Callable<JsonObject> {
  
  private DataObject.DataKind type;
  private List<String> data; 
  
  DataObjectThread(DataObject.DataKind type, List<String> listData) {
    this.type = type;
    this.data = listData;
  }
  
  public JsonObject call() {
    Integer nrElements;
    Map<String, Integer> dataMap = new TreeMap<>();
    for(String singleData: this.data) {
      nrElements= dataMap.get(singleData);
      if(nrElements == null) {
        dataMap.put(singleData, 1);
      }
      else {
        dataMap.put(singleData, nrElements + 1);
      }
    }
    if(type.equals(DataObject.DataKind.HOUR)) {
      //TODO additional job to get data  
    }
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    for(Map.Entry entry: dataMap.entrySet()) {
        jsonObjectBuilder.add(entry.getKey().toString(), entry.getValue().toString());
    }
    return jsonObjectBuilder.build();
  }
  
}
