package ch.pantas.billsplitter.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class EventService {

    @Inject
    private EventStore eventStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private UserService userService;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    public Event removeEventAndGetActiveEvent(Event event) {
        checkNotNull(event);
        checkArgument(!event.isNew());

        removeEvent(event);

        UUID activeEventId = sharedPreferenceService.getActiveEventId();
        if(activeEventId == null) return null;

        if(activeEventId.equals(event.getId())) {
            Event newActiveEvent = getNextActiveEvent();
            UUID newActiveEventId = newActiveEvent != null ? newActiveEvent.getId() : null;
            sharedPreferenceService.storeActiveEventId(newActiveEventId);
            return newActiveEvent;
        } else {
            return eventStore.getById(activeEventId);
        }
    }

    public Event getActiveEvent() {
        UUID eventId = sharedPreferenceService.getActiveEventId();
        if (eventId == null) return null;
        return eventStore.getById(eventId);
    }

    public Event createEvent(String name, SupportedCurrency currency){
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        User me = userService.getMe();
        Event event = new Event(name, currency, me.getId());
        eventStore.persist(event);
        Participant participant = new Participant(me.getId(), event.getId());
        participantStore.persist(participant);
        return event;
    }

    private void removeEvent(Event event){
        eventStore.removeById(event.getId());
        participantStore.removeAll(event.getId());

        for(Expense expense : expenseStore.getExpensesOfEvent(event.getId())){
            attendeeStore.removeAll(expense.getId());
        }

        expenseStore.removeAll(event.getId());
    }

    private Event getNextActiveEvent(){
        List<Event> events = eventStore.getAll();

        if(events.size() == 0) return null;

        return events.get(events.size()-1);
    }
}
