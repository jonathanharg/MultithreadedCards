import java.util.concurrent.BlockingQueue;

public class Deck {

    private int number;
    private BlockingQueue<Card> cards;

    public Deck(int number) {
        this.number = number;
    }

    public String toString(){
//        String.join(", ", cards.toArray())
        return "deck" + number + " contents: " + " TODO";
    }

    public void createLog(){
        System.out.println("TODO: 😂log deck to file");
    }
}
