import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CardGame {
  public static CardGame currentGame;
  private final int n;
  private final Deck[] decks;
  private final Player[] players;

  private Player winner = null;
  private boolean playerHasWon = false;

  public CardGame(int n, Path packPath) throws InvalidPackException, InvalidPlayerNumberException {
    if (1 > n)
      throw new InvalidPlayerNumberException(
          "The game must have a non-zero number of players, but was %d!".formatted(n));
    this.n = n;
    decks = new Deck[n];
    players = new Player[n];
    currentGame = this;

    //creates n number of decks
    for (int i = 0; i < n; i++) {
      decks[i] = new Deck(i + 1);
    }

    //creates n number of cards
    for (int i = 0; i < n; i++) {
      players[i] = new Player(i + 1, decks[i], decks[(i + 1) % n]);
    }

    Card[] cards = loadPack(packPath);
    deal(cards);
  }

  public static void main(String[] args) {
    boolean validNumPlayers = false;
    boolean validPackPath = false;
    int numPlayers = 0;
    Path deckPath = null;

    if (0 < args.length) {
      //takes and validates the number of players
      try {
        numPlayers = Integer.parseInt(args[0]);
        validNumPlayers = true;
      } catch (NumberFormatException e) {
        System.out.println("Invalid number of players passed as argument.");
      }
    }

    if (1 < args.length) {
      // takes and validates the deck path
      try {
        deckPath = Path.of(args[1]);
        validPackPath = true;
      } catch (InvalidPathException e) {
        System.out.println("Invalid deck file passed as argument.");
      }
    }

    do {
      try {
        if (!validNumPlayers) {
          numPlayers = getNumberOfPlayers();
          validNumPlayers = true;
        }
        if (!validPackPath) {
          deckPath = Path.of(getDeckPath());
          validPackPath = true;
        }
        CardGame cardGame = new CardGame(numPlayers, deckPath);
        // cardGame.runSequentialGame();
        cardGame.runThreadedGame();
      } catch (InvalidPackException e) {
        System.out.println(e.getMessage());
        validPackPath = false;
      } catch (InvalidPlayerNumberException e) {
        System.out.println(e.getMessage());
        validNumPlayers = false;
      }
    } while (!validPackPath || !validNumPlayers);
  }

  private static int getNumberOfPlayers() {
    Scanner scanner = new Scanner(System.in);
    boolean givenValidInt = false;
    int numPlayers = 1; // Default value
    while (!givenValidInt) { // Keep asking for a number until a valid one is provided
      System.out.println("Please enter the number of players:");
      try {
        numPlayers = scanner.nextInt();
        if (1 > numPlayers) {
          throw new InputMismatchException(
              "The game must have a non-zero number of players, but was %d!".formatted(numPlayers));
        } else {
          givenValidInt = true;
        }
      } catch (java.util.InputMismatchException e) {
        System.out.println("The number of players must be a positive integer! " + e.getMessage());
        scanner.nextLine();
        scanner.reset();
      }
    }
    return numPlayers;
  }

  private static String getDeckPath() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Please enter the location of the pack to load:");
    return scanner.nextLine();
  }

  public boolean hasPlayerWon() {
    return playerHasWon;
  }

  public void notifyPlayerFinished() {
    // A player has finished implies a player has won.
    playerHasWon = true;
    determineWinner();
  }

  private Card[] loadPack(Path packPath) throws InvalidPackException {
    Card[] cards = new Card[8 * n];
    List<String> lines;
    try {
      lines = Files.readAllLines(packPath);
    } catch (IOException e) {
      throw new InvalidPackException("Error loading pack %s".formatted(packPath), e);
    }
    if (lines.size() != 8 * n) {
      //checks that the deck given is the correct length
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
        String errorString =
            "Invalid card value on line %d of %s. Each line must be a non-negative integer.";
        throw new InvalidPackException(errorString.formatted(i, packPath.toString()));
      }
    }
    return cards;
  }

  private void deal(Card[] cards) {
    for (int i = 0; i < 4 * n; i++) {
      //deals each card in the first half of the pack to the players in a circular order
      players[i % n].addCard(cards[i], i / n);
    }
    for (int i = (8 * n) - 1; i > (4 * n) - 1; i--) {
      // deals each card in the second half of the pack to the decks
      // loops through the cards in reverse order to counteract the use of a linkedBlockingQueue for the decks.
      try {
        decks[i % n].addCard(cards[i]);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void runThreadedGame() {
    for (int i = 0; i < n; i++) {
      players[i].log(
          players[i] + " initial hand " + players[i].handToString(), CREATE, TRUNCATE_EXISTING);
      if (players[i].hasWinningHand() && null == winner) {
        winner = players[i];
        playerHasWon = true;
      }
    }
    for (int i = 0; i < n; i++) {
      Thread player = new Thread(players[i]);
      player.start();
    }
  }

  public void runSequentialGame() {
    for (int i = 0; i < n; i++) {
      players[i].log(
          players[i] + " initial hand " + players[i].handToString(), CREATE, TRUNCATE_EXISTING);
      if (players[i].hasWinningHand() && null == winner) {
        winner = players[i];
        playerHasWon = true;
      }
    }
    while (!playerHasWon) {
      for (int i = 0; i < n; i++) {
        players[i].takeTurn();
        if (players[i].hasWinningHand()) {
          playerHasWon = true;
        }
      }
    }
    determineWinner();
  }

  private void determineWinner() {
    if (null != winner) return;
    for (int i = 0; i < n; i++) {
      if (players[i].hasWinningHand()) {
        winner = players[i];
        break;
      }
    }
    System.out.println(winner + " has won! 🥳😹");
    for (int i = 0; i < n; i++) {
      players[i].finalLog(winner);
      decks[i].createFinalLog();
    }
  }
}

class InvalidPackException extends Exception {
  public InvalidPackException(String str) {
    super(str);
  }

  public InvalidPackException(String str, Throwable cause) {
    super(str, cause);
  }
}

class InvalidPlayerNumberException extends Exception {
  public InvalidPlayerNumberException(String str) {
    super(str);
  }
}
