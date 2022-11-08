import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CardGame {
    public static CardGame currentGame;
    public final int n;
    private final Deck[] decks;
    private final Player[] players;
    private final boolean playerHasWon = false;

    public CardGame(int n, Path packPath) throws InvalidPackException, InvalidPlayerNumberException {
        if (n < 1)
            throw new InvalidPlayerNumberException("The game must have a non-zero number of players, but was %d!".formatted(n));
        this.n = n;
        this.decks = new Deck[n];
        this.players = new Player[n];
        currentGame = this;

        for (int i = 0; i < n; i++) {
            decks[i] = new Deck(i + 1);
        }

        for (int i = 0; i < n; i++) {
            players[i] = new Player(i + 1, decks[i], decks[(i + 1) % n]);
        }

        Card[] cards = loadPack(packPath);
        deal(cards);
    }

    public static void main(String[] args) {
        boolean validNumPlayers = false;
        boolean validPackPath = false;
        int numPlayers = 0;
        Path deckPath = null;

        while (!validPackPath || !validNumPlayers) {
            try {
                if (!validNumPlayers) {
                    numPlayers = askForNumber();
                    validNumPlayers = true;
                }
                if (!validPackPath) {
                    deckPath = Path.of(askForDeckPath());
                    validPackPath = true;
                }

                new CardGame(numPlayers, deckPath);
            } catch (InvalidPackException e) {
                System.out.println(e.getMessage());
                validPackPath = false;
            } catch (InvalidPlayerNumberException e){
                System.out.println(e.getMessage());
                validNumPlayers = false;
            } catch (InvalidPathException e){
                System.out.println(e.getMessage());
                validPackPath = false;
            }
        }
    }

    private static int askForNumber() {
        Scanner scanner = new Scanner(System.in);
        var givenValidInt = false;
        int numPlayers = 1; // Default value
        while (!givenValidInt) {  // Keep asking for a number until a valid one is provided
            System.out.println("Please enter the number of players:");
            try {
                numPlayers = scanner.nextInt();
                if (numPlayers < 1){
                    throw new InputMismatchException("The game must have a non-zero number of players, but was %d!".formatted(numPlayers));
                } else {
                    givenValidInt = true;
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("The number of players must be a positive integer! " + e.getMessage());
                scanner.nextLine();
                scanner.reset();
            }
        }
        return numPlayers;
    }

    private static String askForDeckPath() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the location of the pack to load:");
        return scanner.nextLine();
    }

    public Card[] loadPack(Path packPath) throws InvalidPackException {
        Card[] cards = new Card[8 * n];
        List<String> lines = null;
        try {
            lines = Files.readAllLines(packPath);
        } catch (IOException e) {
            // todo: improve this error message
            throw new InvalidPackException("Error loading pack %s. IO Error: %s.".formatted(packPath, e.getMessage()));
        }
        if (lines.size() != 8 * n) {
            String errorString = "A decks length must be 8n (%d), but the supplied deck was %s.";
            throw new InvalidPackException(errorString.formatted(8 * n, lines.size()));
        }

        for (int i = 0; i < lines.size(); i++) {
            try {
                int cardValue = Integer.parseInt(lines.get(i));
                if (cardValue < 0) {
                    throw new NumberFormatException();
                }
                cards[i] = new Card(cardValue);
            } catch (NumberFormatException e) {
                String errorString = "Invalid card value on line %d of %s. Each line must be a non-negative integer.";
                throw new InvalidPackException(errorString.formatted(i, packPath.toString()));
            }
        }
        return cards;
    }

    public void deal(Card[] cards){
        for (int i = 0; i < 4*n; i++){
            players[i%n].addCard(cards[i], i/n);
        }
        for (int i = (8*n)-1; i > (4*n)-1; i--){
            try {
                decks[i%n].addCard(cards[i]);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



class InvalidPackException extends Exception {
    public InvalidPackException(String str) {
        super(str);
    }
}

class InvalidPlayerNumberException extends Exception {
    public InvalidPlayerNumberException(String str) {
        super(str);
    }
}