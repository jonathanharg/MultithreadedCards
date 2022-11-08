import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import static java.nio.file.StandardOpenOption.CREATE;

public class Deck {

    private final int number;
    private final LinkedBlockingQueue<Card> cards = new LinkedBlockingQueue<>();

    public Deck(int number) {
        this.number = number;
    }

    public String toString(){
        List<String> cardList = cards.stream().map(c -> String.valueOf(c.getValue())).toList();
        return "deck " + number + " contents: " + String.join(" ", cardList);
    }
    public void addCard(Card card) throws InterruptedException {
        cards.put(card);
    }

    public Card takeCard() throws InterruptedException {
        return cards.take();
    }

    public void log(){
        Path path = Path.of("./deck" + number + "_output.txt");
        try {
            Files.writeString(
                    path, this.toString(),
                    CREATE
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
