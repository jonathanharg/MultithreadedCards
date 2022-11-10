import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    static Deck drawDeck;
    static Deck discardDeck;
    static Player player;

    private static <T> Stream<Arguments> decksGenerator(int[][] decks, List<T> result) {
        AtomicInteger i = new AtomicInteger();
        return Arrays.stream(decks).map(deck -> Arguments.of(new Card[]{new Card(deck[0]), new Card(deck[1]), new Card(deck[2]), new Card(deck[3])}, result.get(i.getAndAdd(1))));
    }

    private static Stream<Arguments> hasWonGenerator() {
        int[][] decks = new int[][]{{1, 2, 3, 4}, {2, 2, 2, 2}, {1, 1, 1, 1}, {1, 1, 1, 3},};
        List<Boolean> results = List.of(false, true, true, false);
        return decksGenerator(decks, results);
    }

    private static Stream<Arguments> selectDiscardCardGenerator() {
        int[][] decks = new int[][]{{2, 2, 2, 2}, {1, 2, 2, 2}, {1, 1, 2, 2}, {1, 1, 1, 2}, {1, 5, 6, 7}, {1, 1, 5, 6}, {1, 1, 1, 5}};
        List<Integer> results = List.of(2, 2, 2, 2, 5, 6, 5);
        return decksGenerator(decks, results);
    }

    @BeforeAll
    static void clean() {
        File directory = new File(System.getProperty("user.dir"));
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith("_output.txt")) {
                f.delete();
            }
        }
    }

    @BeforeEach
    void setUp() {
        drawDeck = new Deck(1);
        discardDeck = new Deck(2);
        player = new Player(1, drawDeck, discardDeck);
    }

    @AfterEach
    void afterEach() {
        clean();
    }

    @ParameterizedTest
    @MethodSource("hasWonGenerator")
    void hasWon(Card[] cards, boolean value) {
        for (int i = 0; i < 4; i++) {
            player.addCard(cards[i], i);
        }
        assertEquals(value, player.hasFinished());
    }

    @Test
    void createLog() {
        player.log("Testing Log!", CREATE, TRUNCATE_EXISTING);
        assertTrue(new File("./player1_output.txt").isFile());
        assertTrue(new File("./player1_output.txt").length() > 0);
    }

    @Test
    void appendLog() {
        createLog();
        var length = new File("./player1_output.txt").length();
        player.log("Add another line!");
        assertTrue(length < new File("./player1_output.txt").length());
    }

    @Test
    void addCard() {
        Card card = new Card(93751934);
        player.addCard(card, 0);
        assertTrue(player.handToString().contains(String.valueOf(card.getValue())));
    }

    @Test
    void swapCardByObj() {
        Card a = new Card(1738);
        Card b = new Card(21);
        player.addCard(a, 0);

        assertEquals(a, player.swapCard(b, a));
        assertEquals(b, player.swapCard(null, b));
    }

    @Test
    void swapCardByIndex() {
        Card a = new Card(1738);
        Card b = new Card(21);
        player.addCard(a, 0);

        assertEquals(a, player.swapCard(b, 0));
        assertEquals(b, player.swapCard(null, 0));
    }

    @ParameterizedTest
    @MethodSource("selectDiscardCardGenerator")
    void selectDiscardCard(Card[] cards, Integer value) {
        for (int i = 0; i < 4; i++) {
            player.addCard(cards[i], i);
        }
        assertEquals(value, player.selectDiscardCard().getValue());
    }

    @Test
    void selectDiscardCardNull(){
        for (int i = 0; i < 4; i++) {
            player.addCard(new Card(1), i);
        }
        assertNull(player.selectDiscardCard());
    }

    @Test
    void string(){
        assertEquals("player 1", player.toString());
    }

}