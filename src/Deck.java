import java.util.concurrent.LinkedBlockingQueue;

public class Deck {

    private final int number;
    private final LinkedBlockingQueue<Card> cards = new LinkedBlockingQueue<>();

    public Deck(int number) {
        this.number = number;
    }

    public String toString(){
        return "deck" + number + " contents: " + cards;
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
