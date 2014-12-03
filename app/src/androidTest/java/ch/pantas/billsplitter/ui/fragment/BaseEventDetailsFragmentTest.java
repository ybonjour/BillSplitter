package ch.pantas.billsplitter.ui.fragment;

import android.content.Intent;

import org.mockito.Mock;

import java.util.Arrays;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.EventDetails;

import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;

public abstract class BaseEventDetailsFragmentTest extends BaseEspressoTest<EventDetails> {

    @Mock
    protected SharedPreferenceService sharedPreferenceService;

    @Mock
    protected ActivityStarter activityStarter;

    @Mock
    protected EventStore eventStore;

    @Mock
    protected DebtCalculator debtCalculator;

    @Mock
    protected UserService userService;

    @Mock
    protected ExpenseStore expenseStore;

    protected Event event;

    protected User me;

    public BaseEventDetailsFragmentTest() {
        super(EventDetails.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        me = new User(randomUUID().toString(), "Me");
        when(userService.getMe()).thenReturn(me);

        event = new Event(randomUUID().toString(), "An event", SupportedCurrency.CHF, randomUUID().toString());
        when(eventStore.getById(event.getId())).thenReturn(event);

        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);

        // Ensures that help text is not shown
        Expense expense = new Expense(randomUUID().toString(), event.getId(), me.getId(), "An expense", 100, me.getId());
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));
    }
}