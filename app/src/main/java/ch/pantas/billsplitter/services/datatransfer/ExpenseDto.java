package ch.pantas.billsplitter.services.datatransfer;

import java.util.List;

import ch.pantas.billsplitter.model.Expense;

public class ExpenseDto {
    private Expense expense;
    private List<AttendeeDto> attendingParticipants;

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public List<AttendeeDto> getAttendingParticipants() {
        return attendingParticipants;
    }

    public void setAttendingParticipants(List<AttendeeDto> attendingParticipants) {
        this.attendingParticipants = attendingParticipants;
    }
}
