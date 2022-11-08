import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Deck {

    private int number;
    private LinkedBlockingQueue<Card> cards = new LinkedBlockingQueue<>();

    public Deck(int number) {
        this.number = number;
    }

    public String toString(){
        return "deck" + number + " contents: " + cards.toString();
    }
    public void addCard(Card card) throws InterruptedException {
        cards.put(card);
    }

    public Card takeCard() throws InterruptedException {
        return cards.take();
    }

    public void createLog(){
        System.out.println("TODO: 😂log deck to file");
    }
}
