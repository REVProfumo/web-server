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
  private static final String METHOD_NOT_SUPPORTED = "Method not supported";
  private static final String UNEXPECTED_ERROR = "Unexpected error";

  private final DataObject bom;

  RequestHandler(File file) throws IOException {
    bom = new DataObject(file);
  }

  public void handle(HttpExchange httpExchange) throws IOException {
    String uri = httpExchange.getRequestURI().getPath();
    String pathElements = uri.replace("/", "");
    buildResponse(httpExchange, pathElements);
  }

  private JsonObject getJson(String pathURL) throws ExecutionException, InterruptedException{
    JsonObject responseBuilder = null;
      DataObject.DataKind dataKind = DataObject.DataKind.valueOf(pathURL.toUpperCase());
      switch (dataKind) {
        case DEVICE_ID:
          responseBuilder = bom.getDevices();
          break;
        case HOUR:
          responseBuilder = bom.getHours();
          break;
        case DAY:
          responseBuilder = bom.getDays();
          break;
        case MONTH:
          responseBuilder = bom.getMonths();
      }
    return responseBuilder;
  }

  private void buildResponse(HttpExchange httpExchange, String kind) throws IOException {
    httpExchange.getResponseHeaders().add("encoding", "UTF-8");
    httpExchange.getResponseHeaders().set("Content-Type", "application/json");
    JsonWriter jwriter;
    try {
      JsonObject json = getJson(kind);
      jwriter = Json.createWriter(httpExchange.getResponseBody());
      httpExchange.sendResponseHeaders(OK_CODE, json.toString().getBytes().length);
      jwriter.writeObject(json);
      httpExchange.getResponseBody().flush();
    } catch (IllegalArgumentException e) {
      httpExchange.sendResponseHeaders(400, METHOD_NOT_SUPPORTED.getBytes().length);
      httpExchange.getResponseBody().write(METHOD_NOT_SUPPORTED.getBytes());
      e.printStackTrace();
    }
    catch(ExecutionException | InterruptedException e){
      httpExchange.sendResponseHeaders(500, UNEXPECTED_ERROR.getBytes().length);
      httpExchange.getResponseBody().write(UNEXPECTED_ERROR.getBytes());
      e.printStackTrace();
    }
  }
}
