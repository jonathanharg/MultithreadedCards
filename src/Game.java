public class Game {
    public final int n;
    public static Game currentGame;
    private Deck[] decks;
    private Player[] player;
    private boolean playerHasWon = false;

    public Game(int n) {
        this.n = n;
        this.decks = new Deck[n];
        this.player = new Player[n];
        this.currentGame = this;
    }

    public Card[] loadPack(String file){
        return null;
    }
}
