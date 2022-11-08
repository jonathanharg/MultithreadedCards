import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PlayerTest {
    static Deck drawDeck;
    static Deck discardDeck;
    static Player player;

    private static <T> Stream<Arguments> decksGenerator(int[][] decks, List<T> result) {
        AtomicInteger i = new AtomicInteger();
        return Arrays.stream(decks).map(deck -> {

            return Arguments.of(new Card[]{
                    new Card(deck[0]),
                    new Card(deck[1]),
                    new Card(deck[2]),
                    new Card(deck[3])
            }, result.get(i.getAndAdd(1)));
        });
    }

    private static Stream<Arguments> hasWonGenerator() {
        int[][] decks = new int[][]{
                {1, 2, 3, 4},
                {2, 2, 2, 2},
                {1, 1, 1, 1},
                {1, 1, 1, 3},
        };
        List<Boolean> results = List.of(false, true, true, false);
        return decksGenerator(decks, results);
    }

    private static Stream<Arguments> selectDiscardCardGenerator_NotEquals() {
        int[][] decks = new int[][]{
                {2, 2, 2, 2},
                {1, 2, 3, 4},
                {1, 1, 3, 4},
                {1, 1, 1, 3},
//                {1, 1, 1, 1},
        };
        List<Integer> results = List.of(1, 1, 1, 1);
        return decksGenerator(decks, results);
    }

    private static Stream<Arguments> selectDiscardCardGenerator_Equals() {
        int[][] decks = new int[][]{
                {2, 2, 2, 2},
                {1, 2, 2, 2},
                {1, 1, 2, 2},
                {1, 1, 1, 2},
//                {1, 1, 1, 1},
        };
        List<Integer> results = List.of(2, 2, 2, 2);
        return decksGenerator(decks, results);
    }

    @BeforeEach
    void setUp() {
        drawDeck = new Deck(1);
        discardDeck = new Deck(2);
        player = new Player(1, drawDeck, discardDeck);
    }

    @ParameterizedTest
    @MethodSource("hasWonGenerator")
    void hasWon(Card[] cards, boolean value) {
        for (int i = 0; i < 4; i++) {
            player.addCard(cards[i], i);
        }
        assertEquals(value, player.hasWon());
    }

    @Test
    void run() {
    }

    @Test
    void createLog() {
    }

    @Test
    void addCard() {
    }

    @Test
    void swapCard() {
    }

    @Test
    void testSwapCard() {
    }

    @ParameterizedTest
    @MethodSource("selectDiscardCardGenerator_NotEquals")
    void selectDiscardCard_NotEquals(Card[] cards, Integer value) {
        for (int i = 0; i < 4; i++) {
            player.addCard(cards[i], i);
        }
        assertNotEquals(value, player.selectDiscardCard().getValue());
    }

    @ParameterizedTest
    @MethodSource("selectDiscardCardGenerator_Equals")
    void selectDiscardCard_Equals(Card[] cards, Integer value) {
        for (int i = 0; i < 4; i++) {
            player.addCard(cards[i], i);
        }
        assertEquals(value, player.selectDiscardCard().getValue());
    }

}