package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.adapter.AttendeeAdapter;
import ch.pantas.billsplitter.ui.adapter.PayerAdapter;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasText;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static ch.pantas.billsplitter.ui.AddExpense.ARGUMENT_EXPENSE_ID;
import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
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
    private UserService userService;

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
    private Participant participantMe;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event("abcd", "An Event", EUR, randomUUID().toString());
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);
        me = new User(randomUUID().toString(), "Me");
        when(userService.getMe()).thenReturn(me);
        participantMe = new Participant(randomUUID().toString(), me.getId(), event.getId(), true, 0);
        when(participantStore.getParticipant(event.getId(), me.getId())).thenReturn(participantMe);
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

    private static Matcher<Attendee> newAttendeeWithUserId(final String participantId) {
        return new TypeSafeMatcher<Attendee>() {
            @Override
            public boolean matchesSafely(Attendee attendee) {
                return attendee.isNew()
                        && participantId.equals(attendee.getParticipant());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New Attendee with user ");
                description.appendText(participantId);
            }
        };
    }

    private static Matcher<Expense> newExpenseWith(final String expenseDescription, final int amount, final String eventId, final String participantId) {
        return new TypeSafeMatcher<Expense>() {
            @Override
            public boolean matchesSafely(Expense expense) {
                if (!expense.isNew()) return false;
                if (!expenseDescription.equals(expense.getDescription())) return false;
                if (amount != expense.getAmount()) return false;
                if (!eventId.equals(expense.getEventId())) return false;
                if (participantId != null && !participantId.equals(expense.getPayerId()))
                    return false;

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
