import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataObject {

  public enum DataKind {
    DEVICE_ID, HOUR;

    @Override
    public String toString() {
      return name().toLowerCase();
    }
  }
  
  private Future<JsonObject> dataHoursFuture;
  private Future<JsonObject> dataDevicesFuture;
  
  private JsonObject dataHours;
  private JsonObject dataDevices;
  
  DataObject(File file) throws IOException {
    List<String> device_ids = new ArrayList<>();
    List<String> hours = new ArrayList<>();
    BufferedReader bf = new BufferedReader(new FileReader(file));
    String line;
    String[] elements;
    bf.readLine();
    while ((line = bf.readLine()) != null) {
      elements = line.split(",");
      device_ids.add(elements[0].trim());
      hours.add(elements[3].trim());
    }
    
    Callable<JsonObject> threadDevices = new DataObjectThread(DataKind.DEVICE_ID, device_ids);
    Callable<JsonObject> threadHours = new DataObjectThread(DataKind.HOUR, hours);

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    dataHoursFuture = executorService.submit(threadHours);
    dataDevicesFuture = executorService.submit(threadDevices);
  }

  JsonObject getHours() throws ExecutionException, InterruptedException{
    if(dataHours == null) {
      dataHours = dataHoursFuture.get();
    }
    return dataHours;
  }

  JsonObject getDevices() throws ExecutionException, InterruptedException{
    if(dataDevices == null) {
      dataDevices = dataDevicesFuture.get();
    }
    return dataDevices;  
  }
  
}
