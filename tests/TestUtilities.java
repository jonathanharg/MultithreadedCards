import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class TestUtilities {
  public static void clean() {
    File directory = new File(System.getProperty("user.dir"));
    for (File f : Objects.requireNonNull(directory.listFiles())) {
      if (f.getName().endsWith("_output.txt")) {
        f.delete();
      }
    }
  }

  public static boolean fileEqualsString(String actual, String textFile) throws IOException {
    File directory = new File(System.getProperty("user.dir"));
    String file = Files.readString(Paths.get(directory + "/" + textFile));

    return actual.equals(file);
  }

  public static boolean filesEqual(String textFile1, String textFile2) throws IOException {
    File directory = new File(System.getProperty("user.dir"));
    String file1 = Files.readString(Paths.get(directory + "/" + textFile1));
    String file2 = Files.readString(Paths.get(directory + "/" + textFile2));

    return file1.equals(file2);
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
