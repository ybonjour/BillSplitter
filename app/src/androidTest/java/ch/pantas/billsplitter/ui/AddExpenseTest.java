package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.common.collect.Sets;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.adapter.AttendeeAdapter;
import ch.pantas.billsplitter.ui.adapter.PayerAdapter;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Double.parseDouble;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddExpenseTest extends BaseEspressoTest<AddExpense> {
    public AddExpenseTest() {
        super(AddExpense.class);
    }

    @Mock
    private EventStore eventStore;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private UserStore userStore;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private AttendeeStore attendeeStore;

    @Mock
    private ActivityStarter activityStarter;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Mock
    private PayerAdapter payerAdapter;

    @Mock
    private AttendeeAdapter attendeeAdapter;

    private User me;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event("abcd", "An Event");
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);
        me = new User(UUID.randomUUID().toString(), "Me");
        when(sharedPreferenceService.getUserName()).thenReturn(me.getName());
        when(userStore.getUserWithName(me.getName())).thenReturn(me);
        when(payerAdapter.getSelectedUser()).thenReturn(me);

        when(payerAdapter.getViewTypeCount()).thenReturn(1);
        when(attendeeAdapter.getViewTypeCount()).thenReturn(1);
    }

    @LargeTest
    public void testTitleIsEventName() {
        // When
        getActivity();

        // Then
        onView(withText(event.getName())).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testSaveButtonIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testExpenseIsAddedIfSaveButtonIsPressed() {
        // Given
        String description = "An expense";
        String amount = "25.00";
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));
        onView(withId(R.id.expense_amount)).perform(typeText(amount));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(expenseStore, times(1)).persist(argThat(newExpenseWith(description, parseDouble(amount), event.getId(), me.getId())));
    }

    @LargeTest
    public void testAttendanceIsAddedIfSaveButtonIsPressed(){
        // Given
        String description = "An expense";
        String amount = "25.00";
        User user = new User(UUID.randomUUID().toString(), "Joe");
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));
        onView(withId(R.id.expense_amount)).perform(typeText(amount));
        when(attendeeAdapter.getSelectedUsers()).thenReturn(newHashSet(user));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(attendeeStore, times(1)).persist(argThat(newAttendeeWithUserId(user.getId())));
    }

    @LargeTest
    public void testExpenseIsNotAddedIfSaveButtonIsPressedWithMissingValues() {
        // Given
        String description = "abcdef";
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(userStore, times(0)).persist(any(User.class));
        verify(expenseStore, times(0)).persist(any(Expense.class));
        onView(withId(R.id.expense_amount)).check(hasBackgroundColor(R.color.error_color));
    }

    private static Matcher<Attendee> newAttendeeWithUserId(final String userId){
        return new TypeSafeMatcher<Attendee>() {
            @Override
            public boolean matchesSafely(Attendee attendee) {
                return attendee.isNew()
                        && userId.equals(attendee.getUser());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New Attendee with user ");
                description.appendText(userId);
            }
        };
    }

    private static Matcher<Expense> newExpenseWith(final String expenseDescription, final double amount, final String eventId, final String userId) {
        return new TypeSafeMatcher<Expense>() {
            @Override
            public boolean matchesSafely(Expense expense) {
                if (!expense.isNew()) return false;
                if (!expenseDescription.equals(expense.getDescription())) return false;
                if (amount != expense.getAmount()) return false;
                if (!eventId.equals(expense.getEventId())) return false;
                if (userId != null && !userId.equals(expense.getPayerId())) return false;

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New expense with description ");
                description.appendText(expenseDescription);
                description.appendText(" and amount ");
                description.appendValue(amount);
                description.appendText(" and event ");
                description.appendText(eventId);
            }
        };
    }
}
