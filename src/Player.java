import static java.nio.file.StandardOpenOption.APPEND;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Player implements Runnable {

  private final int number;
  private final Card[] hand = new Card[4];
  private final Deck drawDeck;
  private final Deck discardDeck;

  private final Random random = new Random();

  public Player(int number, Deck drawDeck, Deck discardDeck) {
    this.number = number;
    this.drawDeck = drawDeck;
    this.discardDeck = discardDeck;

    // Makes players "random" choices predictable for debugging & reproducibility purposes.
    random.setSeed(number);
  }

  public boolean hasWinningHand() {
    Card first = hand[0];
    for (int i = 1; 3 >= i; i++) {
      if (first.getValue() != hand[i].getValue()) return false;
    }
    return true;
  }

  public Card selectDiscardCard() {
    Card[] discardCards =
        Arrays.stream(hand).filter(c -> c.getValue() != number).toArray(Card[]::new);
    if (0 < discardCards.length) {
      int randomIndex = random.nextInt(discardCards.length);
      return discardCards[randomIndex];
    } else {
      return null;
    }
  }

  public void takeTurn() {
    try {
      Card newCard = drawDeck.takeCard();
      log(this + " draws a " + newCard + " from deck" + number);
      Card discardCard = selectDiscardCard();
      if (null == discardCard) {
        discardCard = newCard;
      } else {
        swapCard(newCard, discardCard);
      }
      discardDeck.addCard(discardCard);
      log(this + " discards a " + discardCard + " to deck" + (number + 1));
      log(this + " current hand is " + handToString());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void log(String message, OpenOption... options) {
    // log can be called with CREATE and TRUNCATE_EXISTING options to overwrite old log files from a
    // previous game.
    Path path = Path.of(System.getProperty("user.dir") + "/player" + number + "_output.txt");
    try {
      Files.writeString(path, message + System.lineSeparator(), options);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void log(String message) {
    log(message, APPEND); // Log appends by default unless otherwise stated.
  }

  public void finalLog(Player winner) {
    if (winner == this) {
      log(this + " wins");
    } else {
      log(winner + " has informed " + this + " that " + winner + " has won");
    }
    log(this + " exits");
    log(this + " hand: " + handToString());
  }

  public void addCard(Card newCard, int index) {
    hand[index] = newCard;
  }

  public Card swapCard(Card newCard, int index) {
    Card oldCard = hand[index];
    addCard(newCard, index);
    return oldCard;
  }

  public Card swapCard(Card newCard, Card oldCard) {
    int index = Arrays.asList(hand).indexOf(oldCard);
    return swapCard(newCard, index);
  }

  public String handToString() {
    List<String> cards =
        Arrays.stream(hand).map(c -> (null != c) ? String.valueOf(c.getValue()) : "empty").toList();
    return String.join(" ", cards);
  }

  public String toString() {
    return "player " + number;
  }

  @Override
  public void run() {
    while (!CardGame.currentGame.hasPlayerWon()) {
      takeTurn();
      if (hasWinningHand()) {
        CardGame.currentGame.notifyPlayerFinished();
      }
    }
  }
}
