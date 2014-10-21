package ch.pantas.billsplitter.model;

public class Debt {

    private final User from;
    private final User to;
    private final int amount;
    private final Currency currency;

    public Debt(User from, User to, int amount, Currency currency){
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.currency = currency;
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

    public Currency getCurrency() {
        return currency;
    }

    public String getFormattedAmount(){
        return getCurrency().format(getAmount());
    }

    public String toString(){
        return from.getName() + " -> " + to.getName() + ": " + currency.format(amount);
    }
}
