package ch.pantas.billsplitter.model;

public class Attendee extends Model {

    private String expense;
    private String participant;

    public Attendee(String id, String expense, String participant) {
        super(id);
        this.expense = expense;
        this.participant = participant;
    }

    public Attendee(String expense, String participant) {
        this.expense = expense;
        this.participant = participant;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }
}
