import java.util.ArrayList;

public class CardGame {

    public final int n;
    public static CardGame currentGame;
    private Deck[] decks;
    private Player[] player;
    private boolean playerHasWon = false;

    public CardGame(int n) {
        this.n = n;
        this.decks = new Deck[n];
        this.player = new Player[n];
        this.currentGame = this;
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    public Card[] loadPack(String file){
        return null;
    }
}