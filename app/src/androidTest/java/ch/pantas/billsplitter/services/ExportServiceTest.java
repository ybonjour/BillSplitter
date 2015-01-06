package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.AttendeeDto;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.ExpenseDto;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;

import static ch.pantas.billsplitter.framework.CustomMatchers.matchesParticipantDto;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ExportServiceTest extends BaseMockitoInstrumentationTest {
    @Mock
    private EventStore eventStore;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private UserStore userStore;

    @Mock
    private UserService userService;

    @Mock
    private AttendeeStore attendeeStore;

    @Mock
    private ExpenseStore expenseStore;

    @Inject
    private ExportService exportService;

    private Event event;
    private User user;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event(randomUUID(), "An event", EUR, randomUUID());
        when(eventStore.getById(event.getId())).thenReturn(event);

        user = new User(randomUUID(), "Joe");
        when(userStore.getById(user.getId())).thenReturn(user);
    }

    @SmallTest
    public void testExportEventThrowsNullPointerExceptionIfNoEventIdProvided() {
        try {
            exportService.exportEvent(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testExportEventReturnsEventDtoWithCorrectEvent() {
        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        assertEquals(event, eventDto.getEvent());
    }

    @SmallTest
    public void testExportEventReturnsEventDtoWithCorrectParticipants() {
        // Given
        Participant participant = new Participant(randomUUID(), user.getId(), event.getId(), false, 0);
        List<Participant> participants = asList(participant);
        when(participantStore.getParticipants(event.getId())).thenReturn(participants);
        User otherUser = new User(randomUUID(), "Joe");
        when(userService.getMe()).thenReturn(otherUser);

        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        List<ParticipantDto> participantDtos = eventDto.getParticipants();
        assertEquals(1, participantDtos.size());
        assertThat(participantDtos.get(0), matchesParticipantDto(participant.getId(),
                equalTo(user), participant.isConfirmed(), participant.getLastUpdated()));
    }

    @SmallTest
    public void testExportEventReturnsEventDtoWithNoParticipantsIfNoParticipantsExist() {
        // Given
        when(participantStore.getParticipants(event.getId())).thenReturn(new LinkedList<Participant>());

        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        assertNotNull(eventDto.getParticipants());
        assertEquals(0, eventDto.getParticipants().size());
    }

    @SmallTest
    public void testExportEventReturnsMeAsParticipantWithCorrectLastUpdatedValue(){
        // Given
        Participant participant = new Participant(randomUUID(), user.getId(), event.getId(), false, 0);
        List<Participant> participants = asList(participant);
        when(participantStore.getParticipants(event.getId())).thenReturn(participants);
        when(userService.getMe()).thenReturn(user);

        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        List<ParticipantDto> participantDtos = eventDto.getParticipants();
        assertEquals(1, participantDtos.size());
        assertTrue(participantDtos.get(0).getLastUpdated() > participant.getLastUpdated());
    }

    @SmallTest
    public void testExportEventReturnsNoExpensesIfNoExpensesExist(){
        // Given
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(new LinkedList<Expense>());

        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        List<ExpenseDto> expenseDtos = eventDto.getExpenses();
        assertEquals(0, expenseDtos.size());
    }

    @SmallTest
    public void testExportEventReturnsExpenseCorrectlyWithoutAttendees(){
        // Given
        Expense expense = new Expense(randomUUID(), event.getId(), user.getId(), "An event", 1000, user.getId());
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));
        when(attendeeStore.getAttendees(expense.getId())).thenReturn(new LinkedList<Attendee>());

        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        List<ExpenseDto> expenseDtos = eventDto.getExpenses();
        assertEquals(1, expenseDtos.size());
        ExpenseDto expenseDto = expenseDtos.get(0);
        assertEquals(expense, expenseDto.getExpense());
        assertEquals(0, expenseDto.getAttendingParticipants().size());
    }

    @SmallTest
    public void testExportEventReturnsExpenseCorrectlyWithAttendee(){
        // Given
        Participant participant = new Participant(randomUUID(), user.getId(), event.getId(), false, 0);
        Expense expense = new Expense(randomUUID(), event.getId(), user.getId(), "An event", 1000, user.getId());
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));
        Attendee attendee = new Attendee(randomUUID(), expense.getId(), participant.getId());
        when(attendeeStore.getAttendees(expense.getId())).thenReturn(asList(attendee));

        // When
        EventDto eventDto = exportService.exportEvent(event.getId());

        // Then
        assertNotNull(eventDto);
        List<ExpenseDto> expenseDtos = eventDto.getExpenses();
        assertEquals(1, expenseDtos.size());
        ExpenseDto expenseDto = expenseDtos.get(0);
        assertEquals(expense, expenseDto.getExpense());
        assertEquals(1, expenseDto.getAttendingParticipants().size());
        AttendeeDto attendeeDto = expenseDto.getAttendingParticipants().get(0);
        assertEquals(attendee.getId(), attendeeDto.getAttendeeId());
        assertEquals(participant.getId(), attendeeDto.getParticipantId());
    }

}
