import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class DataObject {

  final static int DEVICE_ID = 0;
  final static int LATITUDE = 1;
  final static int LONGITUDE = 2;
  final static int TIMESTAMP = 3;
  
  private final Future<JsonObject> dataHoursFuture;
  private final Future<JsonObject> dataDevicesFuture;
  private final Future<JsonObject> dataDaysFuture;
  private final Future<JsonObject> dataMonthsFuture;

  private JsonObject dataHours;
  private JsonObject dataDevices;
  private JsonObject dataDays;
  private JsonObject dataMonths;
  
  DataObject(File file) throws IOException {
    Map<Integer, List<String>> csvColData = readFromCSV(file, new int[]{DEVICE_ID, TIMESTAMP});
    
    Callable<JsonObject> threadDevices = new DataObjectThread(DataKind.DEVICE_ID, csvColData.get(DEVICE_ID));
    Callable<JsonObject> threadHours = new DataObjectThread(DataKind.HOUR, csvColData.get(TIMESTAMP));
    Callable<JsonObject> threadDays = new DataObjectThread(DataKind.DAY, csvColData.get(TIMESTAMP));
    Callable<JsonObject> threadMonths = new DataObjectThread(DataKind.MONTH, csvColData.get(TIMESTAMP));

    ExecutorService executorService = Executors.newFixedThreadPool(4);
    dataHoursFuture = executorService.submit(threadHours);
    dataDevicesFuture = executorService.submit(threadDevices);
    dataDaysFuture = executorService.submit(threadDays);
    dataMonthsFuture = executorService.submit(threadMonths);

    executorService.shutdown();
  }

  private Map<Integer, List<String>> readFromCSV(File file, int[] columnIndex) throws IOException {
    Map<Integer, List<String>> csvColumns = new HashMap<>();
    
    for (int index : columnIndex) {
      csvColumns.put(index, new ArrayList<String>());
    }
    
    BufferedReader bf = new BufferedReader(new FileReader(file));
    String line;
    String[] elements;
    
    bf.readLine();
    while ((line = bf.readLine()) != null) {
      elements = line.split(",");
      for (int index : columnIndex) {
        csvColumns.get(index).add(elements[index].trim());
      }
    }

    return csvColumns;
  }

  JsonObject getHours() throws ExecutionException, InterruptedException {
    if (dataHours == null) {
      dataHours = dataHoursFuture.get();
    }
    return dataHours;
  }

  JsonObject getDevices() throws ExecutionException, InterruptedException {
    if (dataDevices == null) {
      dataDevices = dataDevicesFuture.get();
    }
    return dataDevices;
  }

  JsonObject getMonths() throws ExecutionException, InterruptedException {
    if (dataMonths == null) {
      dataMonths = dataMonthsFuture.get();
    }
    return dataMonths;
  }

  JsonObject getDays() throws ExecutionException, InterruptedException {
    if (dataDays == null) {
      dataDays = dataDaysFuture.get();
    }
    return dataDays;
  }
}
