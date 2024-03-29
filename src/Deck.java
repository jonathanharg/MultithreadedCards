import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    return "deck " + number;
  }

  public void addCard(Card card) throws InterruptedException {
    // Put blocks the current thread if the queue is locked
    cards.put(card);
  }

  public Card takeCard() throws InterruptedException {
    // Take blocks the current thread until the queue is unlocked and a card is available.
    // or returns null after 200 milliseconds of waiting.
    return cards.poll(200, TimeUnit.MILLISECONDS);
  }

  public void createFinalLog() {
    Path path = Path.of(System.getProperty("user.dir") + "/deck" + number + "_output.txt");
    String deckInfo = this + " contents: " + Card.StreamToString(cards.stream());
    try {
      Files.writeString(
          path, deckInfo, CREATE, TRUNCATE_EXISTING); // Will any overwrite existing files
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
