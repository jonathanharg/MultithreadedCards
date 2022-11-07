public class Card {

    private final int value; //card number

    public Card(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String toString(){
        return "card " + value;
    }
}
