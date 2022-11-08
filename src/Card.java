public class Card {
    private final int value; //card number

    public Card(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString(){
        return "card " + value;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Card other = (Card) obj;

        if (this.value != other.getValue()) {
            return false;
        }

        return true;
    }
}
