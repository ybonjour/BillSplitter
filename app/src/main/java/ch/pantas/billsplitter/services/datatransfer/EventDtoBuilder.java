package ch.pantas.billsplitter.services.datatransfer;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventDtoBuilder {
    @Inject
    private EventStore eventStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private ExpenseStore expenseStore;

    EventDto eventDto;

    public EventDtoBuilder() {
        init();
    }

    private void init() {
        eventDto = new EventDto();
        eventDto.expenses = new LinkedList<ExpenseDto>();

    }

    public EventDto build() {
        return eventDto;
    }

    public void withEventId(String eventId){
        Event event = eventStore.getById(eventId);
        withEvent(event);

        List<User> participants = participantStore.getParticipants(eventId);
        withParticipants(participants);

        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);
        for(Expense expense : expenses) {
            List<User> attendees = attendeeStore.getAttendees(expense.getId());
            withExpense(expense, attendees);
        }
    }

    public void withEvent(Event event) {
        checkNotNull(event);
        eventDto.event = event;
    }

    public void withParticipants(List<User> participants) {
        checkNotNull(participants);
        eventDto.participants = participants;
    }

    public void withExpense(Expense expense, List<User> attendees) {
        checkNotNull(expense);
        checkNotNull(attendees);

        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.expense = expense;
        expenseDto.attendees = attendees;
        eventDto.expenses.add(expenseDto);
    }

    static public String convertToJson(EventDto eventDto) {
        return new Gson().toJson(eventDto);
    }

    static public EventDto createFromJson(String json) {
        Gson gson = new Gson();
        EventDto eventDto = gson.fromJson(json, EventDto.class);

        return eventDto;
    }

}
