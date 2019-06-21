import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class RequestHandler implements HttpHandler {

  private static final int OK_CODE = 200;
  private static final int KO_CODE = 400;
  private static final String METHOD_NOT_SUPPORTED = "Method not supported";
  private static final String UNEXPECTED_ERROR = "Unexpected error";

  private final DataObject bom;

  RequestHandler(File file) throws IOException {
    bom = new DataObject(file);
  }

  public void handle(HttpExchange httpExchange) throws IOException {
    String uri = httpExchange.getRequestURI().getPath();
    String pathElements = uri.replace("/", "");
    buildResponse(httpExchange, OK_CODE, pathElements);
  }

  private void buildResponse(HttpExchange httpExchange, int responseCode, String kind) throws IOException {
    httpExchange.getResponseHeaders().add("encoding", "UTF-8");
    httpExchange.getResponseHeaders().set("Content-Type", "application/json");
    JsonObject responseBuilder;
    JsonWriter jwriter;
    {
      try {
        if (kind.equals(DataObject.DataKind.DEVICE_ID.toString())) {
          responseBuilder = bom.getDevices();
        } else if (kind.equals(DataObject.DataKind.HOUR.toString())) {
          responseBuilder = bom.getHours();
        } else if (kind.equals(DataObject.DataKind.DAY.toString())) {
          responseBuilder = bom.getDays();
        } else if (kind.equals(DataObject.DataKind.MONTH.toString())) {
          responseBuilder = bom.getMonths();
        } else {
          httpExchange.sendResponseHeaders(responseCode, METHOD_NOT_SUPPORTED.getBytes().length);
          httpExchange.getResponseBody().write(METHOD_NOT_SUPPORTED.getBytes());
          return;
        }
        jwriter = Json.createWriter(httpExchange.getResponseBody());
        httpExchange.sendResponseHeaders(responseCode, responseBuilder.toString().getBytes().length);
        jwriter.writeObject(responseBuilder);
        httpExchange.getResponseBody().flush();
      } catch (ExecutionException | InterruptedException e) {
        httpExchange.sendResponseHeaders(500, UNEXPECTED_ERROR.getBytes().length);
        httpExchange.getResponseBody().write(UNEXPECTED_ERROR.getBytes());
        e.printStackTrace();
      }
    }
  }

}
