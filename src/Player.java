import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Player {

    private int number;
    private Card[] hand = new Card[4];
    private Deck drawDeck;
    private Deck discardDeck;
    private ArrayList<String> log;
    private Random random = new Random();

    public Player(int number, Deck drawDeck, Deck discardDeck) {
        this.number = number;
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
    }

    public boolean hasWon(){
        Card first = hand[0];
        for(int i = 1; i <= 3; i++){
            if (first.getValue() != hand[i].getValue()) return false;
        }
        return true;
    }

    public Card selectDiscardCard(){
        var discardCards = Arrays.stream(hand).filter(c -> c.getValue() != number).toArray(Card[]::new);
        if (discardCards.length > 0){
            int randomIndex = random.nextInt(discardCards.length);
            return discardCards[randomIndex];
        } else {
            return null;
        }
    }

    public void takeTurn(){
        try{
            Card newCard = drawDeck.takeCard();
            Card discardCard = hand[0];
//            Card discardCard = selectDiscardCard();
            swapCard(newCard, discardCard);
            discardDeck.addCard(discardCard);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void createLog(){
        //TODO: converts arraylist into file
    }

    public void addCard(Card newCard, int index){
        hand[index] = newCard;
    }

    public Card swapCard(Card newCard, int index){
        Card oldCard = hand[index];
        addCard(newCard, index);
        return oldCard;
    }

    public Card swapCard(Card newCard, Card oldCard){
        int index = Arrays.asList(hand).indexOf(oldCard);
        return swapCard(newCard, index);
    }

    public String toString(){
        return "player" + number;
    }
}
