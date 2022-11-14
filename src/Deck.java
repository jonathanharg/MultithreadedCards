import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Deck {

  private final int number;
  private final LinkedBlockingQueue<Card> cards = new LinkedBlockingQueue<>();

  public Deck(int number) {
    this.number = number;
  }

  public String toString() {
    List<String> cardList = cards.stream().map(c -> String.valueOf(c.getValue())).toList();
    return "deck " + number + " contents: " + String.join(" ", cardList);
  }

  public void addCard(Card card) throws InterruptedException {
    cards.put(card);
  }

  public Card takeCard() throws InterruptedException {
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
