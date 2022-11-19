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

  private Player winner = null;
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

  public boolean hasPlayerWon() {
    return playerHasWon;
  }

  public void notifyPlayerFinished() {
    // Called when a player has a winning hand, it is then verified if they have won or not
    playerHasWon = true;
    determineWinner();
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
      // creates each card using the value of each card in the deck chose by the user.
      try {
        int cardValue = Integer.parseInt(lines.get(i));
        if (0 > cardValue) {
          throw new NumberFormatException();
        }
        cards[i] = new Card(cardValue);
      } catch (NumberFormatException e) {
        // each card value cannot be a negative integer
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
    int lastIndex = (8 * n) - 1;
    int middleIndex = (4 * n) - 1;
    for (int i = lastIndex; i > middleIndex; i--) {
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

    // Runs each player
    for (int i = 0; i < n; i++) {
      threads[i].start();
    }
  }

  private void checkForInstantWin() {
    boolean instantWin = false;
    for (int i = 0; i < n; i++) {
      // logs initial hands of each player
      players[i].log(
          players[i] + " initial hand " + players[i].handToString(), CREATE, TRUNCATE_EXISTING);
      // Checks if a player has already been dealt a winning hand.
      if (players[i].hasWinningHand()) instantWin = true;
    }
    if (instantWin) notifyPlayerFinished();
  }

  public void runSequentialGame() {
    checkForInstantWin();
    while (!playerHasWon) {
      // each player takes a turn in sequential order until one of them wins
      for (int i = 0; i < n; i++) {
        players[i].takeTurn();
        if (players[i].hasWinningHand()) {
          notifyPlayerFinished();
        }
      }
    }
  }

  private void determineWinner() {
    // returns if another player has already claimed they have won
    if (null != winner) return;
    for (int i = 0; i < n; i++) {
      // finds the first player with a winning hand
      if (players[i].hasWinningHand()) {
        winner = players[i];
        break;
      }
    }
    System.out.println(winner + " has won! 🥳😹");
    for (int i = 0; i < n; i++) {
      // Notifies each player who the winner is.
      players[i].finalLog(winner);
      decks[i].createFinalLog();
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
