package ch.pantas.billsplitter.ui.fragment;

import android.content.Context;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.apps.common.testing.ui.espresso.DataInteraction;

import org.hamcrest.Matchers;
import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.ExpensePresentation;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ExpenseService;
import ch.pantas.splitty.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpensesFragmentTest extends BaseEventDetailsFragmentTest {
    private static final User JOE = new User(randomUUID().toString(), "Joe");
    private static final User MARY = new User(randomUUID().toString(), "Mary");

    @Mock
    private AttendeeStore attendeeStore;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private ExpenseService expenseService;

    private Expense expenseA;
    private Expense expenseB;
    private ExpensePresentation expensePresentationA;
    private ExpensePresentation expensePresentationB;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(sharedPreferenceService.getUserId()).thenReturn(me.getId());

        // Given
        expenseA = new Expense("expenseAId", event.getId(), me.getId(), "expenseA", 123, me.getId());
        expenseB = new Expense("expenseBId", event.getId(), me.getId(), "expenseB", 321, JOE.getId());
        expensePresentationA = new ExpensePresentation(me, expenseA, SupportedCurrency.CHF, asList(me, JOE));
        expensePresentationB = new ExpensePresentation(JOE, expenseB, SupportedCurrency.CHF, asList(me, JOE));
        when(expenseService.getExpensePresentations(event.getId())).thenReturn(asList(expensePresentationA, expensePresentationB));

        // When
        getActivity();
        onView(withId(R.id.event_details_content)).perform(swipeLeft());
    }

    @SmallTest
    public void testCorrectExpensesAreShown() {

        // Then
        onExpensePresentation(expensePresentationA).check(matches(isDisplayed()));
        onExpensePresentation(expensePresentationB).check(matches(isDisplayed()));
    }

    @SmallTest
    public void testEditOwnedExpense() {

        // When
        onExpensePresentation(expensePresentationA).perform(click());

        // Then
        verify(activityStarter, times(1)).startEditExpense(any(Context.class), eq(expenseA));
    }

    @SmallTest
    public void testEditNotOwnedExpense() {

        // When
        onExpensePresentation(expensePresentationB).perform(click());

        // Then
        verify(activityStarter, times(0)).startEditExpense(any(Context.class), any(Expense.class));
    }

    private DataInteraction onExpensePresentation(ExpensePresentation ep){
        return onData(Matchers.<Object>equalTo(ep)).inAdapterView(withId(R.id.expenses_list));
    }
}

