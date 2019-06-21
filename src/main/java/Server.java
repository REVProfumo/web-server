import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Server {
  
  public static void main(String... args){
    try {
      int port =  8081;
      File file = new File("src/main/resources/dataset.csv");
      HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", new RequestHandler(file));
      server.setExecutor(Executors.newCachedThreadPool());
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
