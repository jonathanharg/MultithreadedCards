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
//        String.join(", ", cards.toArray())
        return "deck" + number + " contents: " + " TODO";
    }
    public void addCard(Card card){
        cards.add(card);
    }

    public void createLog(){
        System.out.println("TODO: 😂log deck to file");
    }
}
