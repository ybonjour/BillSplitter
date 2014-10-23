package ch.pantas.billsplitter.services.datatransfer;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

public class EventDtoBuilder {
    EventDto eventDto;

    public EventDtoBuilder() {
        init();
    }

    public EventDtoBuilder(Event event, List<User> participants) {
        init();
        setEvent(event);
        setParticipants(participants);
    }

    private void init() {
        eventDto = new EventDto();
        eventDto.expensesList = new LinkedList<Expense>();
        eventDto.attendeesMap = new HashMap<Expense, List<User>>();

    }

    public EventDto create() {
        return eventDto;
    }

    public void setEvent(Event event) {
        eventDto.event = event;
    }

    public void setParticipants(List<User> participants) {
        eventDto.participantsList = participants;
    }

    public void addExpense(Expense expense, List<User> attendees) {
        eventDto.expensesList.add(expense);
        eventDto.attendeesMap.put(expense, attendees);
    }

}
