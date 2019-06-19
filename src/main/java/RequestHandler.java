import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RequestHandler implements HttpHandler {

  private static final String DEVICE = "device";
  private static final String HOUR = "hour";
  
  private static final int OK_CODE = 200;
  private static final int KO_CODE = 400;
  
  private static final String URI_REGEX = "^\\\\?[a-zA-Z]*=[0-9]*$";
  
  public void handle(HttpExchange httpExchange) throws IOException {
    String uri = httpExchange.getRequestURI().getQuery();
    Pattern uriPattern = Pattern.compile(URI_REGEX);
    Matcher matcher = uriPattern.matcher(uri);
    if(!matcher.matches()) {
      buildResponse(httpExchange, KO_CODE, -1);
    }
    String[] pathElements = uri.replace("?","").split("=");
    
    if (pathElements.length == 2) {
      if (pathElements[0].equals(DEVICE)) {
        int nrDevices = 0;
        //TODO implement findNrDevices();
        buildResponse(httpExchange, OK_CODE, nrDevices);
      } else if (pathElements[0].equals(HOUR)) {
        int nrInOneHour = 0;
        //TODO implement findNrInOneHour();
        buildResponse(httpExchange, OK_CODE, nrInOneHour);
      }
    } else {
      buildResponse(httpExchange, KO_CODE, -1);
    }
  }

  private void buildResponse(HttpExchange he, int responseCode, int nr) throws IOException {
    he.getResponseHeaders().add("encoding", "UTF-8");
    he.getResponseHeaders().set("Content-Type", "application/json");
    String response = String.valueOf(nr);
    he.sendResponseHeaders(responseCode, response.length());
    //TODO build body with json
    he.getResponseBody().write(response.getBytes());
    he.close();
  }

}
