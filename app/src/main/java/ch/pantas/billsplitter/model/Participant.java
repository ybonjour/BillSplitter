package ch.pantas.billsplitter.model;

public class Participant extends Model {

    private String expense;
    private String user;

    public Participant(String id, String expense, String user) {
        super(id);
        this.expense = expense;
        this.user = user;
    }

    public Participant(String expense, String user) {
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
