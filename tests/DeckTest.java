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
  static void beforeAll() {
    TestUtilities.clean();
  }

  @BeforeEach
  void beforeEach() throws Exception {
    // adds cards to the deck before each test
    deck = new Deck(1);
    deck.addCard(new Card(1));
    deck.addCard(new Card(7));
    deck.addCard(new Card(2));
    deck.addCard(new Card(9));
  }

  @AfterEach
  void afterEach() {
    // clears the test utilities after each test
    TestUtilities.clean();
  }

  @Test
  void testAddCard() throws IOException {
    // tests that addCard() used in @BeforeEach correctly adds cards to the deck in the right order.
    deck.createFinalLog();
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 1 7 2 9", "deck1_output.txt"));
  }

  @Test
  void takeCard() throws Exception {
    //tests that the contents of a deck are as expected.
    assertEquals(1, deck.takeCard().getValue());
    assertEquals(7, deck.takeCard().getValue());
    assertEquals(2, deck.takeCard().getValue());
    assertEquals(9, deck.takeCard().getValue());

    deck.createFinalLog();
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: ", "deck1_output.txt"));
  }

  @Test
  void testCreateFinalLog() throws Exception {
    // tests that the final log of a deck is correct.
    deck.createFinalLog();
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 1 7 2 9", "deck1_output.txt"));
  }
}
