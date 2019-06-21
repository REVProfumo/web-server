import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class RequestHandler implements HttpHandler {

  private static final int OK_CODE = 200;
  private static final int KO_CODE = 400;
  
  private static final String KO = "KO";
  
  private final DataObject bom;

  RequestHandler(File file) throws IOException {
    bom = new DataObject(file);  
  }

  public void handle(HttpExchange httpExchange) throws IOException {
    String uri = httpExchange.getRequestURI().getPath();
    String pathElements = uri.replace("/", "");
    if (pathElements.equals(DataObject.DataKind.DEVICE_ID.toString())) {
      buildResponse(httpExchange, OK_CODE, DataObject.DataKind.DEVICE_ID);
    } else if (pathElements.equals(DataObject.DataKind.HOUR.toString())) {
      buildResponse(httpExchange, OK_CODE, DataObject.DataKind.HOUR);
    } else {
      buildResponse(httpExchange, KO_CODE, null);
    }
  }

  private void buildResponse(HttpExchange httpExchange, int responseCode, DataObject.DataKind kind) throws IOException {
    httpExchange.getResponseHeaders().add("encoding", "UTF-8");
    httpExchange.getResponseHeaders().set("Content-Type", "application/json");
    JsonObject responseBuilder;
    JsonWriter jwriter;
    if(kind == null) {
      //response = KO;
      jwriter = null;
    }
    else {
      try {
        responseBuilder = kind.equals(DataObject.DataKind.HOUR) ? bom.getHours() : bom.getDevices();
        jwriter = Json.createWriter(httpExchange.getResponseBody());
        httpExchange.sendResponseHeaders(responseCode, responseBuilder.toString().getBytes().length);
        httpExchange.getResponseBody().write(responseBuilder.toString().getBytes());
        jwriter.writeObject(responseBuilder);
        httpExchange.getResponseBody().flush();
        jwriter.close();
        httpExchange.close();
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
