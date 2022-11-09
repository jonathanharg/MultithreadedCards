import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardGameTest {

    public static Object getPrivateField(CardGame game, String field) throws NoSuchFieldException, IllegalAccessException {
        var privateField = game.getClass().getDeclaredField(field);
        privateField.setAccessible(true);
        return privateField.get(game);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 0, -1, -5})
    void invalidPlayerInput(int playerNumbers) {
        assertThrows(InvalidPlayerNumberException.class, () -> new CardGame(playerNumbers, Path.of(".nonExistentDeck")));
    }

    @Test
    void mainArgsPlayerNumber() {
        String[] args = {String.valueOf(2), System.getProperty("user.dir") + "/tests/resources/2pl_simple.txt"};
    }

    @Test
    void mainArgsDeckPath() {
        String[] args = {String.valueOf(5), System.getProperty("user.dir") + "/tests/resources/5pl_verifiedOutput.txt"};
    }

    @Test
    void deal() {
        CardGame game = null;
        try {
            game = new CardGame(3, Path.of(System.getProperty("user.dir") + "/tests/resources/3pl_unique_infinite.txt"));
            Player[] players = (Player[]) getPrivateField(game, "players");
            Deck[] decks = (Deck[]) getPrivateField(game, "decks");

            assertEquals("1 4 7 10", players[0].handToString());
            assertEquals("2 5 8 11", players[1].handToString());
            assertEquals("3 6 9 12", players[2].handToString());

            assertEquals("deck 1 contents: 22 19 16 13", decks[0].toString());
            assertEquals("deck 2 contents: 23 20 17 14", decks[1].toString());
            assertEquals("deck 3 contents: 24 21 18 15", decks[2].toString());

        } catch (InvalidPackException | InvalidPlayerNumberException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}