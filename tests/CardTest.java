import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CardTest {
  @Test
  void testEquals() {
    Card a = new Card(5);
    Card b = new Card(5);
    Card c = new Card(6);
    String d = "d";

    assertEquals(a, b);
    assertNotEquals(a, c);
    assertNotEquals(a, d);
    assertNotEquals(a, null);
  }
}
