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
    DEVICE_ID("device"), HOUR("hour"), DAY("day"), MONTH("month");

    @Override
    public String toString() {
      return name().toLowerCase();
    }
    
    public final String label;
    private DataKind(String label) {
      this.label = label;
    }
  }
  
  private final Future<JsonObject> dataHoursFuture;
  private final Future<JsonObject> dataDevicesFuture;
  private final Future<JsonObject> dataDaysFuture;
  private final Future<JsonObject> dataMonthsFuture;

  private JsonObject dataHours;
  private JsonObject dataDevices;
  private JsonObject dataDays;
  private JsonObject dataMonths;
  
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
    Callable<JsonObject> threadDays = new DataObjectThread(DataKind.DAY, hours);
    Callable<JsonObject> threadMonths = new DataObjectThread(DataKind.MONTH, hours);
    
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    dataHoursFuture = executorService.submit(threadHours);
    dataDevicesFuture = executorService.submit(threadDevices);
    dataDaysFuture = executorService.submit(threadDays);
    dataMonthsFuture = executorService.submit(threadMonths);

    executorService.shutdown();
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

  JsonObject getMonths() throws ExecutionException, InterruptedException{
    if(dataMonths == null) {
      dataMonths = dataMonthsFuture.get();
    }
    return dataMonths;
  }

  JsonObject getDays() throws ExecutionException, InterruptedException{
    if(dataDays == null) {
      dataDays = dataDaysFuture.get();
    }
    return dataDays;
  }
  
}
