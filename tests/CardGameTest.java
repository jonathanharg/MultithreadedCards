import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CardGameTest {

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, 0, -1, -5})
  void invalidPlayerInput(int playerNumbers) {
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
    assertThrows(CardGame.InvalidPackException.class, () -> new CardGame(3, Path.of(deck)));
  }

  @Test
  void deal() throws Exception {
    CardGame game;
    game =
        new CardGame(
            3,
            Path.of(System.getProperty("user.dir") + "/tests/resources/3pl_unique_infinite.txt"));
    Player[] players = (Player[]) TestUtilities.getPrivateField(game, "players");
    Deck[] decks = (Deck[]) TestUtilities.getPrivateField(game, "decks");

    assertEquals("1 4 7 10", players[0].handToString());
    assertEquals("2 5 8 11", players[1].handToString());
    assertEquals("3 6 9 12", players[2].handToString());

    for (int i = 0; 3 > i; i++) {
      decks[i].createFinalLog();
    }

    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 22 19 16 13", "deck1_output.txt"));
    assertTrue(TestUtilities.fileEqualsString("deck 2 contents: 23 20 17 14", "deck2_output.txt"));
    assertTrue(TestUtilities.fileEqualsString("deck 3 contents: 24 21 18 15", "deck3_output.txt"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"2pl_simple.txt", "3pl_instant_win.txt", "5pl.txt"})
  void Test2PlayerSimple(String deck) throws Exception {
    File local = new File(System.getProperty("user.dir"));
    CardGame game = new CardGame(deck.charAt(0) - '0', Path.of(local + "/tests/resources/" + deck));
    game.runThreadedGame();
    Thread.sleep(500); // wait for game to finish
  }

  @Test
  void limitTest() throws Exception {
    Random random = new Random();
    int max = 5;
    for (int i = 0; max > i; i++) {
      int n = random.nextInt(2, 101);
      System.out.println("Testing " + i + "/" + max + " for " + n + " players");
      generateValidDeck(n);
      CardGame game = new CardGame(n, Path.of("./deck" + n + "_generated.txt"));
      game.runThreadedGame();
      while (game.isRunning()) {
        Thread.sleep(500);
      }

      Thread.sleep(2000);
      //      assertNotNull(TestUtilities.getPrivateField(game, "winner"));

      new File("./deck" + n + "_generated.txt").delete();
    }
  }

  void generateValidDeck(int n) throws IOException {
    Random random = new Random();
    ArrayList<Integer> cards = new ArrayList<>();
    for (int i = 1; i <= n; i++) {
      cards.add(i);
      cards.add(i);
      cards.add(i);
      cards.add(i);
    }
    for (int i = 1; i <= 4 * n; i++) {
      cards.add(random.nextInt(2, 101));
    }
    Collections.shuffle(cards);
    FileWriter writer = new FileWriter("./deck" + n + "_generated.txt");
    for (Integer i : cards) {
      writer.write(i + System.lineSeparator());
    }
    writer.close();
  }
}
