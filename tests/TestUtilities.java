import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class TestUtilities {
  public static void clean() {
    File directory = new File(System.getProperty("user.dir"));
    for (File f : directory.listFiles()) {
      if (f.getName().endsWith("_output.txt")) {
        f.delete();
      }
    }
  }

  public static <T> Stream<Arguments> decksGenerator(int[][] decks, List<T> result) {
    AtomicInteger i = new AtomicInteger();
    return Arrays.stream(decks)
        .map(
            deck ->
                Arguments.of(
                    new Card[] {
                      new Card(deck[0]), new Card(deck[1]), new Card(deck[2]), new Card(deck[3])
                    },
                    result.get(i.getAndAdd(1))));
  }

  public static Object getPrivateField(Object obj, String field)
      throws NoSuchFieldException, IllegalAccessException {
    var privateField = obj.getClass().getDeclaredField(field);
    privateField.setAccessible(true);
    return privateField.get(obj);
  }

  public static Object runPrivateMethod(Object obj, String method, Object... params)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    var privateMethod = obj.getClass().getDeclaredMethod(method);
    privateMethod.setAccessible(true);
    return privateMethod.invoke(obj, params);
  }
}
