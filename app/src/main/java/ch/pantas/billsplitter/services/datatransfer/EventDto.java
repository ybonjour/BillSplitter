package ch.pantas.billsplitter.services.datatransfer;

import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

public class EventDto {
    public Event event;

    public List<User> participants;
    public List<ExpenseDto> expenses;
}
