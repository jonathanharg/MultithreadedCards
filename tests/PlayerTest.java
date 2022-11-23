import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PlayerTest {
  static Deck drawDeck;
  static Deck discardDeck;
  static Player player;

  private static Stream<Arguments> hasWonGenerator() {
    // generates decks, including ones that have won.
    int[][] decks =
        new int[][] {
          {1, 2, 3, 4}, {2, 2, 2, 2}, {1, 1, 1, 1}, {1, 1, 1, 3},
        };
    List<Boolean> results = List.of(false, true, true, false);
    return TestUtilities.decksGenerator(decks, results);
  }

  private static Stream<Arguments> selectDiscardCardGenerator() {
    // generates discard cards.
    int[][] decks =
        new int[][] {
          {2, 2, 2, 2},
          {1, 2, 2, 2},
          {1, 1, 2, 2},
          {1, 1, 1, 2},
          {1, 5, 6, 7},
          {1, 1, 5, 6},
          {1, 1, 1, 5}
        };
    List<Integer> results = List.of(2, 2, 2, 2, 5, 6, 5);
    return TestUtilities.decksGenerator(decks, results);
  }

  @BeforeAll
  static void beforeAll() {
    // ensures utilities are cleaned before all tests.
    TestUtilities.clean();
  }

  @AfterEach
  void afterEach() {
    // ensures utilities are cleaned before each test.
    TestUtilities.clean();
  }

  @BeforeEach
  void beforeEach() {
    drawDeck = new Deck(1);
    discardDeck = new Deck(2);
    player = new Player(1, drawDeck, discardDeck);
  }

  @ParameterizedTest
  @MethodSource("hasWonGenerator")
  void hasWonTest(Card[] cards, boolean value) {
    // tests that the game recognises when a player has won.
    for (int i = 0; 4 > i; i++) {
      player.addCard(cards[i], i);
    }
    assertEquals(value, player.hasWinningHand());
  }

  @Test
  void createLogTest() {
    // tests that logs are successfully created
    player.log("Testing Log!", CREATE, TRUNCATE_EXISTING);
    assertTrue(new File("./player1_output.txt").isFile());
    assertTrue(0 < new File("./player1_output.txt").length());
  }

  @Test
  void appendLogTest() {
    // tests that a player can add another line to its log successfully
    createLogTest();
    var length = new File("./player1_output.txt").length();
    player.log("Add another line!");
    assertTrue(length < new File("./player1_output.txt").length());
  }

  @Test
  void addCardTest() {
    // tests that a card can be added to a player's hand.
    Card card = new Card(93751934);
    player.addCard(card, 0);
    assertTrue(player.handToString().contains(String.valueOf(card.getValue())));
  }

  @Test
  void swapCardByObjTest() {
    // tests that cards can be swapped when given their object
    Card a = new Card(1738);
    Card b = new Card(21);
    player.addCard(a, 0);

    assertEquals(a, player.swapCard(b, a));
    assertEquals(b, player.swapCard(null, b));
  }

  @Test
  void swapCardByIndexTest() {
    // tests that cards can be swapped when given their index
    Card a = new Card(1738);
    Card b = new Card(21);
    player.addCard(a, 0);

    assertEquals(a, player.swapCard(b, 0));
    assertEquals(b, player.swapCard(null, 0));
  }

  @ParameterizedTest
  @MethodSource("selectDiscardCardGenerator")
  void selectDiscardCard(Card[] cards, Integer value) throws Exception {
    // tests that cards can be discarded successfully
    for (int i = 0; 4 > i; i++) {
      player.addCard(cards[i], i);
    }
    Card discardCard = (Card) TestUtilities.runPrivateMethod(player, "selectDiscardCard");
    assertEquals(value, discardCard.getValue());
  }

  @Test
  void selectDiscardCardNull() throws Exception {
    //
    for (int i = 0; 4 > i; i++) {
      player.addCard(new Card(1), i);
    }
    Card discardCard = (Card) TestUtilities.runPrivateMethod(player, "selectDiscardCard");
    assertNull(discardCard);
  }

  @Test
  void string() {
    assertEquals("player 1", player.toString());
  }
}
