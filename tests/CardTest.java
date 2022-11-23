import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CardTest {
  @Test
  void equalsTest() {
    Card a = new Card(5);
    Card b = new Card(5);
    Card c = new Card(6);
    String d = "d";

    assertEquals(a, b);
    assertNotEquals(a, c);
    assertNotEquals(a, d);
    assertNotEquals(a, null);
  }

  @Test
  void stringTest() {
    Card a = new Card(978);
    assertEquals("978", a.toString());
  }

  @Test
  void streamToStringTest() {
    List<Card> hand = Arrays.asList(new Card(1), new Card(9), new Card(8), new Card(4));
    assertEquals("1 9 8 4", Card.StreamToString(hand.stream()));
  }

}
