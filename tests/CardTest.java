import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void testEquals() {
        Card a = new Card(5);
        Card b = new Card(5);
        Card c = new Card( 6);

        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}