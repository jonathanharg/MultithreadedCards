import java.util.List;
import java.util.stream.Stream;

public class Card {
  private final int value; // card number

  public Card(int value) {
    this.value = value;
  }

  public static String StreamToString(Stream<Card> cardStream) {
    List<String> cards =
        cardStream.map(c -> (null != c) ? String.valueOf(c.getValue()) : "empty").toList();
    return String.join(" ", cards);
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }

    if (obj.getClass() != getClass()) {
      return false;
    }

    Card other = (Card) obj;

    // Two cards are equal if their values are equal
    return value == other.getValue();
  }
}
