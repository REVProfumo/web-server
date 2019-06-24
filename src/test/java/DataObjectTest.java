import org.junit.Test;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class DataObjectTest {

  private static final Map<Integer, Integer> dummyMap = new HashMap<Integer, Integer>() {{
    put(6581, 11);
    put(6580, 4);
    put(6582, 4);
    put(6585, 1);
    put(6570, 4);
    put(6571, 1);
    put(6589, 2);
    put(6578, 1);
  }};

  @Test
  public void testDatoObjectBuild() throws IOException, ExecutionException, InterruptedException {
    File file = new File("src/test/resources/datasetTest.csv");
    DataObject dataObjectTested = new DataObject(file);
    JsonObject jsonObject = dataObjectTested.getDevices();
    for (Map.Entry<Integer, Integer> dummyEntry : dummyMap.entrySet()) {
      assertEquals("" + jsonObject.getInt(dummyEntry.getKey().toString()), "" + dummyEntry.getValue());
    }
  }
}