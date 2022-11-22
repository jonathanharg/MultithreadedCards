import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CardGame {
  public static CardGame currentGame;
  private final int n; // number of players
  private final Deck[] decks;
  private final Player[] players;

  private volatile boolean playerHasWon = false;

  public CardGame(int n, Path packPath) throws InvalidPackException, InvalidPlayerNumberException {
    if (1 > n)
      throw new InvalidPlayerNumberException(
          "The game must have a non-zero number of players, but was %d!".formatted(n));
    this.n = n;
    // There will always be n players and n decks.
    decks = new Deck[n];
    players = new Player[n];
    currentGame = this;

    // creates n number of decks
    for (int i = 0; i < n; i++) {
      decks[i] = new Deck(i + 1);
    }

    // creates n number of players
    for (int i = 0; i < n; i++) {
      players[i] = new Player(i + 1, decks[i], decks[(i + 1) % n]);
    }

    Card[] cards = loadPack(packPath);
    deal(cards);
  }

  public static void main(String[] args) {
    int numPlayers = getNumberOfPlayers();
    CardGame cardGame;
    boolean validDeck = false;
    while (!validDeck) {
      try {
        Path deckPath = getDeckPath();
        cardGame = new CardGame(numPlayers, deckPath);
        validDeck = true;
        // cardGame.runSequentialGame(); // A sequential version of the game, for debugging purposes
        cardGame.runThreadedGame();
      } catch (InvalidPackException | InvalidPlayerNumberException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private static int getNumberOfPlayers() {
    while (true) {
      Scanner scanner = new Scanner(System.in);
      // Keeps asking for a number until a valid one is provided
      System.out.println("Please enter the number of players:");
      try {
        int numPlayers = scanner.nextInt();
        if (1 > numPlayers) {
          // only allows the user to enter a number of players greater than one
          throw new InvalidPlayerNumberException(
              "The game must have a non-zero number of players, but was %d!".formatted(numPlayers));
        } else {
          return numPlayers;
        }
      } catch (InputMismatchException | InvalidPlayerNumberException e) {
        // only allows the user to enter a positive number of players
        System.out.println("The number of players must be a positive integer! " + e.getMessage());
      }
      scanner.close();
    }
  }

  private static Path getDeckPath() {
    // takes in the path of the deck from the user, note. the deck is not necessarily valid yet
    Scanner scanner = new Scanner(System.in);
    System.out.println("Please enter the location of the pack to load:");
    Path path = Path.of(scanner.nextLine());
    scanner.close();
    return path;
  }

  public boolean isRunning() {
    return !playerHasWon;
  }

  public void claimVictoryFor(Player player) {
    // Called when a player has a winning hand, it is then verified if they have won or not
    if (!playerHasWon) {
      playerHasWon = true;
      System.out.println(player + " has won! 🥳😹");
      for (int i = 0; i < n; i++) {
        players[i].finalLog(player);
        decks[i]
            .createFinalLog(); // TODO: MAY BE BUGGED! WAIT FOR THE END OF EACH PLAYERS TURN BEFORE
                               // PRINTING DECKS
      }
    }
  }

  private Card[] loadPack(Path packPath) throws InvalidPackException {
    // loads the pack of 8n cards given by the user
    Card[] cards = new Card[8 * n];
    List<String> lines;
    try {
      lines = Files.readAllLines(packPath);
    } catch (IOException e) {
      throw new InvalidPackException("Error loading pack %s".formatted(packPath), e);
    }
    // checks that the deck given is the correct length
    if (lines.size() != 8 * n) {
      String errorString = "A decks length must be 8n (%d), but the supplied deck was %s.";
      throw new InvalidPackException(errorString.formatted(8 * n, lines.size()));
    }

    for (int i = 0; i < lines.size(); i++) {
      try {
        int cardValue = Integer.parseInt(lines.get(i));
        if (0 > cardValue) {
          throw new NumberFormatException();
        }
        cards[i] = new Card(cardValue);
      } catch (NumberFormatException e) {
        // each card value must be a positive integer, e.g. not a string or -2
        String errorString =
            "Invalid card value on line %d of %s. Each line must be a non-negative integer.";
        throw new InvalidPackException(errorString.formatted(i, packPath.toString()));
      }
    }
    return cards;
  }

  private void deal(Card[] cards) {
    for (int i = 0; i < 4 * n; i++) {
      // deals each card in the first half of the pack to the players in a circular order
      players[i % n].addCard(cards[i], i / n);
    }
    int lastCardInPack = (8 * n) - 1;
    int middleCardInPack = (4 * n) - 1;
    for (int i = lastCardInPack; i > middleCardInPack; i--) {
      // deals each card in the second half of the pack to the decks
      // loops through the cards in reverse order to counteract the FIFO nature of the queue used
      // for the decks.
      try {
        decks[i % n].addCard(cards[i]);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void runThreadedGame() {
    checkForInstantWin();
    if (playerHasWon) return;

    Thread[] threads = new Thread[n];
    // Creates threads for each player
    for (int i = 0; i < n; i++) {
      threads[i] = new Thread(players[i]);
    }

<<<<<<< HEAD
    // Runs each player.
    // The creation and running are separated into two different loops so player 1 doesn't
    // get a significant advantage by running before all the other players threads are even
    // created.
=======
    // Runs each player
    // creation and running are seperated into two different loops so player 1 doesn't get a large
    // advantage by running
    // before all the other players threads are even created.
>>>>>>> 1838c2889c5d9617407f138364350f74013d3609
    for (int i = 0; i < n; i++) {
      threads[i].start();
    }
  }

  private void checkForInstantWin() {
    for (int i = 0; i < n; i++) {
      // logs initial hands of each player
      players[i].log(
          players[i] + " initial hand " + players[i].handToString(), CREATE, TRUNCATE_EXISTING);
    }

    for (int i = 0; i < n; i++) {
      // Checks if a player has already been dealt a winning hand.
      if (players[i].hasWinningHand()) claimVictoryFor(players[i]);
    }
  }

  public void runSequentialGame() {
    checkForInstantWin();
    while (!playerHasWon) {
      // each player takes a turn in sequential order until one of them wins
      for (int i = 0; i < n; i++) {
        players[i].takeTurn();
        if (players[i].hasWinningHand()) {
          claimVictoryFor(players[i]);
        }
      }
    }
  }

  static class InvalidPackException extends Exception {
    public InvalidPackException(String str) {
      super(str);
    }

    public InvalidPackException(String str, Throwable cause) {
      super(str, cause);
    }
  }

  static class InvalidPlayerNumberException extends Exception {
    public InvalidPlayerNumberException(String str) {
      super(str);
    }
  }
}
