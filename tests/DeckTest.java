import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeckTest {

  static Deck deck;

  @BeforeAll
  static void beforeAll() {
    TestUtilities.clean();
  }

  @BeforeEach
  void beforeEach() throws Exception {
    deck = new Deck(1);

    deck.addCard(new Card(1));
    deck.addCard(new Card(7));
    deck.addCard(new Card(2));
    deck.addCard(new Card(9));
  }

  @AfterEach
  void afterEach() {
    TestUtilities.clean();
  }

  @Test
  void testAddCard() {
    assertEquals("deck 1 contents: 1 7 2 9", deck.toString());
  }

  @Test
  void takeCard() throws Exception {

    assertEquals(1, deck.takeCard().getValue());
    assertEquals(7, deck.takeCard().getValue());
    assertEquals(2, deck.takeCard().getValue());
    assertEquals(9, deck.takeCard().getValue());
    assertEquals("deck 1 contents: ", deck.toString());
  }

  @Test
  void testCreateFinalLog() throws Exception {
    deck.createFinalLog();
    File directory = new File(System.getProperty("user.dir"));

    String log = Files.readString(Paths.get(directory + "/deck1_output.txt"));
    assertEquals("deck 1 contents: 1 7 2 9", log);
  }
}
