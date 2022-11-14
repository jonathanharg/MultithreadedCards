public class Card {
  private final int value; // card number

  public Card(int value) {
    this.value = value;
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
