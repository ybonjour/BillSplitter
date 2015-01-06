package ch.pantas.billsplitter.services;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.EventDtoOperator;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;

public class ImportServiceBaseTest extends BaseMockitoInstrumentationTest {
    @Mock
    protected EventStore eventStore;

    @Mock
    protected ExpenseStore expenseStore;

    @Mock
    protected AttendeeStore attendeeStore;

    @Mock
    protected ParticipantStore participantStore;

    @Mock
    protected UserStore userStore;

    @Inject
    protected ImportService importService;

    protected User me;
    protected Event event;
    protected EventDto eventDto;
    protected EventDtoOperator eventDtoOperator;
    protected User user;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        me = new User(randomUUID(), "Me");
        when(userStore.getById(me.getId())).thenReturn(me);

        eventDto = new EventDto();

        UUID ownerId = randomUUID();
        event = new Event(randomUUID(), "An event", SupportedCurrency.CHF, ownerId);
        when(eventStore.getById(event.getId())).thenReturn(event);
        eventDto.setEvent(event);

        user = new User(randomUUID(), "Joe");
        when(userStore.getById(user.getId())).thenReturn(user);

        eventDtoOperator = new EventDtoOperator(eventDto);
    }

    protected Participant addParticipant(EventDto eventDto, User user) {
        return addParticipant(eventDto, user, false, 0);
    }

    protected Participant addParticipant(EventDto eventDto, User user, boolean confirmed, long lastUpdated) {
        Participant participant = new Participant(randomUUID(), user.getId(), event.getId(), confirmed, lastUpdated);
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setUser(user);
        participantDto.setParticipantId(participant.getId());
        participantDto.setLastUpdated(participant.getLastUpdated());
        participantDto.setConfirmed(participant.isConfirmed());
        eventDto.addParticipant(participantDto);

        return participant;
    }
}
