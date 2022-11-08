import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Player {

    private final int number;
    private final Card[] hand = new Card[4];
    private final Deck drawDeck;
    private final Deck discardDeck;
    private ArrayList<String> log;
    private final Random random = new Random();

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

    public void log(String message){
        Path path = Path.of("./player" + number + "_output.txt");
        try {
            Files.writeString(
                    path,
                    message + System.lineSeparator(),
                    CREATE, APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finalLog(){
        log("player %s exits".formatted(number));
        log("player %s hand: %s".formatted(number, handToString()));
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

    private String handToString(){
        var cards = Arrays.stream(hand).map(c -> String.valueOf(c.getValue())).toList();
        return String.join(", ", cards);
    }

    public String toString(){
        return "player" + number;
    }
}
