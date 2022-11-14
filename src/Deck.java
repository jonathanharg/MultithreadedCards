import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Deck {

  private final int number;

  // Here we use a queue as decks, like queues, operate on a First-In First-Out basis. A linked
  // blocking queue is a special form of a queue that has a lock for both the head and the tail.
  // This is ideal for this scenario as it means two players can both add to and remove from a deck
  // at the same time.
  private final LinkedBlockingQueue<Card> cards = new LinkedBlockingQueue<>();

  public Deck(int number) {
    this.number = number;
  }

  public String toString() {
    List<String> cardList = cards.stream().map(c -> String.valueOf(c.getValue())).toList();
    return "deck " + number + " contents: " + String.join(" ", cardList);
  }

  public void addCard(Card card) throws InterruptedException {
    // Put is used as it blocks the current thread if the queue is locked until the queue is
    // unlocked
    cards.put(card);
  }

  public Card takeCard() throws InterruptedException {
    // Take blocks the current thread until the cards queue is unlocked and a card is available to
    // be taken.
    return cards.take();
  }

  public void createFinalLog() {
    Path path = Path.of(System.getProperty("user.dir") + "/deck" + number + "_output.txt");
    try {
      Files.writeString(
          path, toString(), CREATE, TRUNCATE_EXISTING); // Will any overwrite existing files
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
