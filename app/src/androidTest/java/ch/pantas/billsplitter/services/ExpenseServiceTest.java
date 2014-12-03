package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.framework.CustomMatchers;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.model.SupportedCurrency.CHF;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpenseServiceTest extends BaseMockitoInstrumentationTest {

    private static final User JOE = new User(randomUUID().toString(), "Joe");
    private static final User MARY = new User(randomUUID().toString(), "Mary");

    @Inject
    private ExpenseService expenseService;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private UserStore userStore;

    @Mock
    private EventStore eventStore;

    @Mock
    private AttendeeStore attendeeStore;


    private Map<User, Participant> participants = new HashMap<User, Participant>();

    @SmallTest
    public void testCreatePaybackExpenseThrowsNullPointerExceptionIfNoDebtProvided() {
        try {
            expenseService.createPaybackExpense(null, eventWithParticipants());
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCreatePaybackExpenseThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            expenseService.createPaybackExpense(debt(JOE, MARY, 10), null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCreatePaybackExpenseCreatesExpenseCorrectly(){
        // Given
        Event event = eventWithParticipants(JOE, MARY);
        Debt debt = debt(JOE, MARY, 100);

        // When
        expenseService.createPaybackExpense(debt, event);

        // Then
        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseStore).persist(expenseCaptor.capture());
        Expense expense = expenseCaptor.getValue();
        assertEquals(participants.get(JOE).getId(), expense.getPayerId());
        assertEquals(debt.getAmount(), expense.getAmount());

        ArgumentCaptor<Attendee> attendeeCaptor = ArgumentCaptor.forClass(Attendee.class);
        verify(attendeeStore).persist(attendeeCaptor.capture());
        Attendee attendee = attendeeCaptor.getValue();
        assertEquals(expense.getId(), attendee.getExpense());
        assertEquals(participants.get(MARY).getId(), attendee.getParticipant());
    }

    private Event eventWithParticipants(User... users) {
        Event event = new Event(randomUUID().toString(), "An event", CHF, randomUUID().toString());

        for (User user : users) {
            Participant participant = new Participant(randomUUID().toString(), user.getId(), event.getId(), false, 0);
            participants.put(user, participant);
            when(participantStore.getParticipant(event.getId(), user.getId())).thenReturn(participant);
        }

        return event;
    }

    private Debt debt(User from, User to, int amount) {
        return new Debt(from, to, amount, CHF);
    }
}