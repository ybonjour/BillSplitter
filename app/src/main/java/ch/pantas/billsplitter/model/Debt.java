package ch.pantas.billsplitter.model;

public class Debt {

    private final User from;
    private final User to;
    private final int amount;

    public Debt(User from, User to, int amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

    public String toString(){
        return from.getName() + " -> " + to.getName() + ": " + amount;
    }
}
