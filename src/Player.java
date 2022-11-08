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
            if (first != hand[i]) return false;
        }
        return true;
    }

    private Card selectDiscardCard(){
        Card[] discardCards = (Card[]) Arrays.stream(hand).filter(x -> x.getValue() == number).toArray();
        int randomIndex = random.nextInt(discardCards.length);
        return discardCards[randomIndex];
    }

    public void run(){
        Card newCard = drawDeck.cards.take();
        Card discardCard = selectDiscardCard();
        swapCard(newCard, discardCard);
        discardDeck.cards.put(discardCard);
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
