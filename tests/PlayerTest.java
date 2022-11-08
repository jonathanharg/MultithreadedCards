import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    static Deck drawDeck;
    static Deck discardDeck;
    static Player player;

    @BeforeEach
    void setUp() {
        drawDeck = new Deck(1);
        discardDeck = new Deck(2);
        player = new Player(1, drawDeck, discardDeck);
    }

    @ParameterizedTest
    @MethodSource("hasWonGenerator")
    void hasWon(Card[] cards, boolean value) {
        for(int i = 0; i < 4; i++){
            player.addCard(cards[i], i);
        }
        assertEquals(value, player.hasWon());
    }

    private static Stream<Arguments> hasWonGenerator() {
        return Stream.of(
                Arguments.of(new Card[]{
                        new Card(1),
                        new Card(2),
                        new Card(3),
                        new Card(4)
                }, false),
                Arguments.of(new Card[]{
                        new Card(2),
                        new Card(2),
                        new Card(2),
                        new Card(2)
                }, true),
                Arguments.of(new Card[]{
                        new Card(1),
                        new Card(1),
                        new Card(1),
                        new Card(1)
                }, true),
                Arguments.of(new Card[]{
                        new Card(1),
                        new Card(1),
                        new Card(1),
                        new Card(3)
                }, false)
                );
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
}