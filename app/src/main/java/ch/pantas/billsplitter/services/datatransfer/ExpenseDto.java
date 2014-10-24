package ch.pantas.billsplitter.services.datatransfer;

import java.util.List;

import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

public class ExpenseDto {
    public Expense expense;
    public List<AttendeeDto> attendingParticipants;
}
