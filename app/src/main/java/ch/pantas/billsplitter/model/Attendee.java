package ch.pantas.billsplitter.model;

import java.util.UUID;

public class Attendee extends Model {

    private UUID expense;
    private UUID participant;

    public Attendee(UUID id, UUID expense, UUID participant) {
        super(id);
        this.expense = expense;
        this.participant = participant;
    }

    public Attendee(UUID expense, UUID participant) {
        this.expense = expense;
        this.participant = participant;
    }

    public UUID getExpense() {
        return expense;
    }

    public void setExpense(UUID expense) {
        this.expense = expense;
    }

    public UUID getParticipant() {
        return participant;
    }

    public void setParticipant(UUID participant) {
        this.participant = participant;
    }
}
