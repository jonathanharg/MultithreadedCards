import java.util.ArrayList;

public class Player {

    private int number;
    private Card[] hand = new Card[4];
    private Deck drawDeck;
    private Deck discardDeck;
    private ArrayList<String> log;

    public Player(int number, Deck drawDeck, Deck discardDeck) {
        this.number = number;
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
    }

    public boolean hasWon(){
        return false;
        //TODO: loops over thingy to see if they've won
    }

    private Card selectDiscardCard(){
        return null;
        // TOdo:
    }

    public void run(){
        //TODO: runs player, makes decisions
    }

    public void createLog(){
        //TODO: converts arraylist into file
    }

    public void addCard(Card card, int index){
        hand[index] = card;
    }

    public Card swapCard(Card card, int index){
        Card oldCard = hand[index];
        addCard(card, index);
        return oldCard;
    }

    public String toString(){
        return "player" + number;
    }
}
