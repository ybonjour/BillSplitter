package ch.pantas.billsplitter.services.datatransfer;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
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

    @Inject
    private UserStore userStore;

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

        List<Participant> participants = participantStore.getParticipants(eventId);
        withParticipants(participants);

        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);
        for(Expense expense : expenses) {
            List<Attendee> attendees = attendeeStore.getAttendees(expense.getId());
            List<AttendeeDto> attendingParticipants = new LinkedList<AttendeeDto>();
            for (Attendee attendee : attendees) {
                AttendeeDto attendeeDto = new AttendeeDto();
                attendeeDto.attendeeId = attendee.getId();
                attendeeDto.participantId = attendee.getParticipant();
                attendingParticipants.add(attendeeDto);
            }
            withExpense(expense, attendingParticipants);
        }
    }

    public void withEvent(Event event) {
        checkNotNull(event);
        eventDto.event = event;
    }

    public void withParticipants(List<Participant> participants) {
        checkNotNull(participants);
        eventDto.participants = new LinkedList<ParticipantDto>();
        for (Participant participant : participants) {
            ParticipantDto participantDto = new ParticipantDto();
            participantDto.participantId = participant.getId();

            User user = userStore.getById(participant.getUserId());
            participantDto.user = user;

            participantDto.confirmed = participant.isConfirmed();

            eventDto.participants.add(participantDto);
        }
    }

    public void withExpense(Expense expense, List<AttendeeDto> attendingParticipants) {
        checkNotNull(expense);
        checkNotNull(attendingParticipants);

        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.expense = expense;
        expenseDto.attendingParticipants = attendingParticipants;
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
