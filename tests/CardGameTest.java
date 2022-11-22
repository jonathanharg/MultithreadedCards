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
  void invalidPlayerInputTest(int playerNumbers) {
    // tests the player number input is thrown if n < 2.
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
  void invalidDeckTest(String deck) {
    //tests that the deck input is valid.
    assertThrows(CardGame.InvalidPackException.class, () -> new CardGame(3, Path.of(deck)));
  }

  @Test
  void dealTest() throws Exception {
    //tests that the deal() function works correctly in terms of each card being given
    //to the right individual player first, and the player deck.
    CardGame game;
    game =
        new CardGame(
            3,
            Path.of(System.getProperty("user.dir") + "/tests/resources/3pl_unique_infinite.txt"));
    Player[] players = (Player[]) TestUtilities.getPrivateField(game, "players");
    Deck[] decks = (Deck[]) TestUtilities.getPrivateField(game, "decks");

    //here we are testing that each player gets the cards it should.
    assertEquals("1 4 7 10", players[0].handToString());
    assertEquals("2 5 8 11", players[1].handToString());
    assertEquals("3 6 9 12", players[2].handToString());

    for (int i = 0; 3 > i; i++) {
      decks[i].createFinalLog();
    }

    //here we are asserting that the decks are given the correct cards after the players.
    assertTrue(TestUtilities.fileEqualsString("deck 1 contents: 22 19 16 13", "deck1_output.txt"));
    assertTrue(TestUtilities.fileEqualsString("deck 2 contents: 23 20 17 14", "deck2_output.txt"));
    assertTrue(TestUtilities.fileEqualsString("deck 3 contents: 24 21 18 15", "deck3_output.txt"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"2pl_simple.txt", "3pl_instant_win.txt", "5pl.txt"})
  void prePreparedDecksTest(String deck) throws Exception {
    //tests the game with various pre-prepared decks specified in the ValueSource
    //in order to cover a wide scope of decks.
    File local = new File(System.getProperty("user.dir"));
    CardGame game = new CardGame(deck.charAt(0) - '0', Path.of(local + "/tests/resources/" + deck));
    game.runThreadedGame();
    //by using sleep() we ensure that the thread waits enough time for game to finish.
    Thread.sleep(500);
  }

  @Test
  void limitTest() throws Exception {
    //tests the game with randomly generated valid card decks using generateValidDeck().
    Random random = new Random();
    int max = 5;
    for (int i = 0; max > i; i++) {
      //generates a random number of players to test between 2 and 100.
      int n = random.nextInt(2, 101);
      System.out.println("Testing " + i + "/" + max + " for " + n + " players");
      generateValidDeck(n);
      CardGame game = new CardGame(n, Path.of("./deck" + n + "_generated.txt"));
      game.runThreadedGame();
<<<<<<< Updated upstream
      while (game.isRunning()) {
=======
<<<<<<< HEAD
      while (!game.hasPlayerWon()) {
        //by using sleep() we ensure that the thread waits enough time for game to finish.
=======
      while (game.isRunning()) {
>>>>>>> 1838c2889c5d9617407f138364350f74013d3609
>>>>>>> Stashed changes
        Thread.sleep(500);
      }
      Thread.sleep(2000);
<<<<<<< Updated upstream
      //      assertNotNull(TestUtilities.getPrivateField(game, "winner"));

      new File("./deck" + n + "_generated.txt").delete();
=======
<<<<<<< HEAD
      //tests that a player has won the game.
      assertNotNull(TestUtilities.getPrivateField(game, "winner"));
      //deletes the randomly generated deck from this test.
      new File("./generated/deck" + n + "_generated.txt").delete();
=======
      //      assertNotNull(TestUtilities.getPrivateField(game, "winner"));

      new File("./deck" + n + "_generated.txt").delete();
>>>>>>> 1838c2889c5d9617407f138364350f74013d3609
>>>>>>> Stashed changes
    }
  }

  void generateValidDeck(int n) throws IOException {
    //generates valid decks to be used whilst testing.
    //these decks ensure that it is possible for each player to win.
    Random random = new Random();
    ArrayList<Integer> cards = new ArrayList<>();
    //for each player, 4 cards that equal its value are added to the initial deck
    // in order to ensure that it is possible for each player to win.
    for (int i = 1; i <= n; i++) {
      cards.add(i);
      cards.add(i);
      cards.add(i);
      cards.add(i);
    }
    //for the rest of the cards, random numbers are added to the deck.
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
