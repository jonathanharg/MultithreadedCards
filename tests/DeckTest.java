import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeckTest {

  static Deck deck;

  @BeforeAll
  static void beforeAllTest() {
    TestUtilities.clean();
  }

  @BeforeEach
  void beforeEachTest() throws Exception {
    // tests that put() used in the add card function blocks the current
    // thread if the queue is locked.
    deck = new Deck(1);
    deck.addCard(new Card(1));
    deck.addCard(new Card(7));
    deck.addCard(new Card(2));
    deck.addCard(new Card(9));
  }

  @AfterEach
  void afterEachTest() {
    TestUtilities.clean();
  }

  @Test
  void addCardTest() throws IOException {
    deck.createFinalLog();
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 1 7 2 9", "deck1_output.txt"));
  }

  @Test
  void takeCardTest() throws Exception {
    // tests that the takeCard() removes the desired card.
    assertEquals(1, deck.takeCard().getValue());
    assertEquals(7, deck.takeCard().getValue());
    assertEquals(2, deck.takeCard().getValue());
    assertEquals(9, deck.takeCard().getValue());
    //by this point the deck should be empty.
    deck.createFinalLog();
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: ", "deck1_output.txt"));
  }

  @Test
  void finalLogTest() throws Exception {
    deck.createFinalLog();
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 1 7 2 9", "deck1_output.txt"));
  }
}
