package ch.pantas.billsplitter.model;

public class Debt {

    private final User from;
    private final User to;
    private final double amount;

    public Debt(User from, User to, double amount){
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

    public double getAmount() {
        return amount;
    }

    public String toString(){
        return from.getName() + " -> " + to.getName() + ": " + amount;
    }
}
