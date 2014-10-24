package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.framework.CustomMatchers.matchesDebt;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DebtCalculatorTest extends BaseMockitoInstrumentationTest {

    @Inject
    private DebtCalculator debtCalculator;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private UserStore userStore;

    @Mock
    private AttendeeStore attendeeStore;

    @Mock
    private DebtOptimizer debtOptimizer;

    @Mock
    private ParticipantStore participantStore;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        event = new Event(randomUUID().toString(), "My Event", EUR);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(debtOptimizer).optimize(anyList(), eq(EUR));
    }

    @SmallTest
    public void testCalculateDebtsThrowsNullPointerExceptionIfNoEventIsProvided() {
        try {
            debtCalculator.calculateDebts(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCalculateDebtsReturnsEmptyListIfNoExpensesExist() {
        // Given
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(new LinkedList<Expense>());

        // When
        List<Debt> result = debtCalculator.calculateDebts(event);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @SmallTest
    public void testCalculateDebtsReturnsExpenseCorrectly() {
        // Given
        User payer = new User(randomUUID().toString(), "David");
        when(userStore.getById(payer.getId())).thenReturn(payer);

        User attendee = new User(randomUUID().toString(), "Joe");
        when(userStore.getById(attendee.getId())).thenReturn(attendee);

        Participant participantPayer = new Participant(randomUUID().toString(), payer.getId(), "eventId");
        Participant participant = new Participant(randomUUID().toString(), attendee.getId(), "eventId");
        when(participantStore.getById(participantPayer.getId())).thenReturn(participantPayer);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        int amount = 20;
        Expense expense = new Expense(randomUUID().toString(), event.getId(), participantPayer.getId(), "Food", amount);
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));

        when(attendeeStore.getAttendingParticipants(expense.getId())).thenReturn(asList(participant));

        // When
        List<Debt> result = debtCalculator.calculateDebts(event);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertThat(result, hasItem(matchesDebt(attendee, payer, amount / 2)));
    }

    @SmallTest
    public void testCalculateDebtsOptimizesDebts() {
        // Given
        User payer = new User(randomUUID().toString(), "David");
        when(userStore.getById(payer.getId())).thenReturn(payer);

        User attendee = new User(randomUUID().toString(), "Joe");
        when(userStore.getById(attendee.getId())).thenReturn(attendee);

        Participant participantPayer = new Participant(randomUUID().toString(), payer.getId(), "eventId");
        Participant participant = new Participant(randomUUID().toString(), attendee.getId(), "eventId");
        when(participantStore.getById(participantPayer.getId())).thenReturn(participantPayer);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        int amount = 20;
        Expense expense = new Expense(randomUUID().toString(), event.getId(), participantPayer.getId(), "Food", amount);
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));

        when(attendeeStore.getAttendingParticipants(expense.getId())).thenReturn(asList(participant));

        // When
        List<Debt> result = debtCalculator.calculateDebts(event);

        // Then
        verify(debtOptimizer, times(1)).optimize(result, EUR);
    }
}
