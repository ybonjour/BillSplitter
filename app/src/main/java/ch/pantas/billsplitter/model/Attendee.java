package ch.pantas.billsplitter.model;

public class Attendee extends Model {

    private String expense;
    private String user;

    public Attendee(String id, String expense, String user) {
        super(id);
        this.expense = expense;
        this.user = user;
    }

    public Attendee(String expense, String user) {
        this.expense = expense;
        this.user = user;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
