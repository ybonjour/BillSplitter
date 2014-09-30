package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import ch.pantas.billsplitter.ActivityStarter;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpenseListTest extends BaseEspressoTest<ExpensesList> {

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private EventStore eventStore;

    @Mock
    private ActivityStarter activityStarter;

    private Event event;

    public ExpenseListTest() {
        super(ExpensesList.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, "abc");
        setActivityIntent(intent);

        event = new Event("abc", "An event");
        when(eventStore.getById("abc")).thenReturn(event);
    }

    @LargeTest
    public void testAddButtonIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_expense)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testAddExpenseActivityIsOpenWhenAddIsPressed() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.add_expense)).perform(click());

        // Then
        verify(activityStarter, times(1)).startAddExpense(any(Context.class), eq(event));
    }

    @LargeTest
    public void testTitleIsEventName() {
        // When
        getActivity();

        // Then
        onView(withText(event.getName())).check(matches(isDisplayed()));
    }

}
