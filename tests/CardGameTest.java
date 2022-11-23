import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CardGameTest {

  @AfterEach
  void afterEach() {
    TestUtilities.clean();
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, 0, -1, -5, -10})
  void invalidPlayerInput(int playerNumbers) {
    //tests that an in valid player input is thrown when creating a game
    assertThrows(
        CardGame.InvalidPlayerNumberException.class,
        () -> new CardGame(playerNumbers, Path.of(".nonExistentDeck")));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "./tests/resources/3pl_invalid.txt",
        "./tests/resources/3pl_invalid2.txt",
        "./madeUpDeck",
        "5pl.txt"
      })
  void invalidDeck(String deck) {
    // tests that invalid deck paths are thrown when creating a game.
    assertThrows(CardGame.InvalidPackException.class, () -> new CardGame(3, Path.of(deck)));
  }

  @Test
  void dealTest() throws Exception {
    //tests that the deal function works correctly and deals cards in a round-robin fashion
    CardGame game;
    game =
        new CardGame(
            3,
            Path.of(System.getProperty("user.dir") + "/tests/resources/3pl_unique_infinite.txt"));
    Player[] players = (Player[]) TestUtilities.getPrivateField(game, "players");
    Deck[] decks = (Deck[]) TestUtilities.getPrivateField(game, "decks");

    //firstly we test that the first half of the pack- are players' hands are as they should be?
    assertEquals("1 4 7 10", players[0].handToString());
    assertEquals("2 5 8 11", players[1].handToString());
    assertEquals("3 6 9 12", players[2].handToString());

    //secondly we test the second half of the pack- are the deck contents correct despite using storing them
    // in a queue?
    for (int i = 0; 3 > i; i++) {
      decks[i].createFinalLog();
    }
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 22 19 16 13", "deck1_output.txt"));
    assertTrue(TestUtilities.fileEqualsString("deck 2 contents: 23 20 17 14", "deck2_output.txt"));
    assertTrue(TestUtilities.fileEqualsString("deck 3 contents: 24 21 18 15", "deck3_output.txt"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"1pl_whitespace_deck.txt", "2pl_simple.txt", "3pl_instant_win.txt", "5pl.txt"})
  void preMadeDeckTest(String deck) throws Exception {
    // testing whether the game runs with pre-made deck paths
    int firstChar = deck.charAt(0) - '0';
    File local = new File(System.getProperty("user.dir"));
    CardGame game = new CardGame(firstChar, Path.of(local + "/tests/resources/" + deck));
    game.runThreadedGame();
  }

  @Test
  void loadPackTest() {
    File local = new File(System.getProperty("user.dir"));
    assertThrows(CardGame.InvalidPackException.class, () -> {
      CardGame game = new CardGame(5, Path.of(local + "/tests/resources/2pl_simple.txt"));
    });
  }

  @Test
  void fileOutputsTest() throws Exception {
    // testing that the file outputs are correct once the game has finished.
    File local = new File(System.getProperty("user.dir"));
    CardGame game = new CardGame(2, Path.of(local + "/tests/resources/2pl_simple.txt"));
    // we run this sequentially rather than with threads as the outcome is determinate.
    game.runSequentialGame();
    assertTrue(TestUtilities.filesEqual("tests/resources/correct_deck1.txt", "deck1_output.txt"));
    assertTrue(TestUtilities.filesEqual("tests/resources/correct_deck2.txt", "deck2_output.txt"));
    assertTrue(TestUtilities.filesEqual("tests/resources/correct_player1.txt", "player1_output.txt"));
    assertTrue(TestUtilities.filesEqual("tests/resources/correct_player2.txt", "player2_output.txt"));
  }

  @Test
  void limitTest() throws Exception {
    //testing the limits of the game with random values 5 times.
    Random random = new Random();
    int max = 5;
    for (int i = 0; max > i; i++) {
      int n = random.nextInt(2, 101);
      System.out.println("Testing " + i + "/" + max + " for " + n + " players");
      generateValidDeck(n);
      CardGame game = new CardGame(n, Path.of("./deck" + n + "_generated.txt"));
      game.runThreadedGame();
      assertNotNull(TestUtilities.getPrivateField(game, "winner"));
      new File("./deck" + n + "_generated.txt").delete();
    }
  }

  @ParameterizedTest
  @ValueSource(
          strings = {"1pl_whitespace_deck.txt", "2pl_simple.txt", "3pl_instant_win.txt", "5pl.txt"})
  void threadRunsTest(String deck) throws Exception {
    // testing that the thread stop running once the game has finnished.
    File local = new File(System.getProperty("user.dir"));
    CardGame game = new CardGame(deck.charAt(0) - '0', Path.of(local + "/tests/resources/" + deck));
    game.runThreadedGame();
    assertFalse(game.isRunning());
  }

  void generateValidDeck(int n) throws IOException {
    //method to create a valid deck that is randomised but still allows a player to win
    Random random = new Random();
    ArrayList<Integer> cards = new ArrayList<>();
    // for each player, four cards of its desired value are added to the deck
    for (int i = 1; i <= n; i++) {
      cards.add(i);
      cards.add(i);
      cards.add(i);
      cards.add(i);
    }
    // for the second half of the deck, random cards are added
    for (int i = 1; i <= 4 * n; i++) {
      cards.add(random.nextInt(2, 101));
    }
    // the deck contents is then shuffled
    Collections.shuffle(cards);
    FileWriter writer = new FileWriter("./deck" + n + "_generated.txt");
    for (Integer i : cards) {
      writer.write(i + System.lineSeparator());
    }
    writer.close();
  }
}
