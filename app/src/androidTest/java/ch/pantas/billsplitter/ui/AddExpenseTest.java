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

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasText;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static ch.pantas.billsplitter.ui.AddExpense.ARGUMENT_EXPENSE_ID;
import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Double.parseDouble;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.argThat;
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

        event = new Event("abcd", "An Event", EUR);
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);
        me = new User(randomUUID().toString(), "Me");
        when(userService.getMe()).thenReturn(me);
        participantMe = new Participant(randomUUID().toString(), me.getId(), event.getId());
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
        String amount = "25.0";
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));
        onView(withId(R.id.expense_amount)).perform(typeText(amount));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        int savedAmount = (int) parseDouble(amount) * 100;
        verify(expenseStore, times(1)).persist(argThat(newExpenseWith(description, savedAmount, event.getId(), participantMe.getId())));
    }

    @LargeTest
    public void testAttendanceIsAddedIfSaveButtonIsPressed() {
        // Given
        String description = "An expense";
        String amount = "25.0";
        User user = new User(randomUUID().toString(), "Joe");
        Participant participant = new Participant(randomUUID().toString(), user.getId(), event.getId());
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));
        onView(withId(R.id.expense_amount)).perform(typeText(amount));
        when(attendeeAdapter.getSelectedUsers()).thenReturn(newHashSet(user));
        when(participantStore.getParticipant(event.getId(), user.getId())).thenReturn(participant);

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

    @LargeTest
    public void testEditExpenseLoadsExistingValues() {
        // Given
        User payer = new User("payerId", "Payer");
        Expense expense = new Expense("expenseId", event.getId(), payer.getId(), "desc", 123);

        User attendee1 = new User("att1", "attendee1");
        User attendee2 = new User("att2", "attendee2");
        User nonAttendee1 = new User("nonatt1", "nonAttendee1");
        User nonAttendee2 = new User("nonatt2", "nonAttendee2");

        List<User> allUsers = new LinkedList<User>();
        allUsers.add(payer);
        allUsers.add(attendee1);
        allUsers.add(attendee2);
        allUsers.add(nonAttendee1);
        allUsers.add(nonAttendee2);

        List<Participant> allParticipants = new LinkedList<Participant>();
        for (User user : allUsers) {
            allParticipants.add(new Participant("participantId" + user.getId(), user.getId(), event.getId()));
        }

        List<User> nonPayerList = new LinkedList<User>();
        nonPayerList.add(attendee1);
        nonPayerList.add(attendee2);
        nonPayerList.add(nonAttendee1);
        nonPayerList.add(nonAttendee2);

        List<User> attendeeList = new LinkedList<User>();
        attendeeList.add(payer);
        attendeeList.add(attendee1);
        attendeeList.add(attendee2);

        List<Participant> attendingParticipants = allParticipants;
        attendingParticipants.remove(attendingParticipants.size() - 1);
        attendingParticipants.remove(attendingParticipants.size() - 1);

        Set<User> attendeeSet = new HashSet<User>(attendeeList);

        for (User user : allUsers) {
            when(userStore.getById(user.getId())).thenReturn(user);
        }
        when(expenseStore.getById(expense.getId())).thenReturn(expense);
        when(participantStore.getParticipants(event.getId())).thenReturn(allParticipants);
        when(attendeeStore.getAttendees(expense.getId())).thenReturn(attendingParticipants);
        when(payerAdapter.getSelectedUser()).thenReturn(payer);
        when(payerAdapter.filterOutSelectedUser(anyList())).thenReturn(nonPayerList);
        when(attendeeAdapter.getSelectedUsers()).thenReturn(attendeeSet);

        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EXPENSE_ID, expense.getId());
        setActivityIntent(intent);

        // When
        getActivity();

        // Then
        verify(payerAdapter, times(1)).select(eq(payer));
        for (User user : attendeeList) {
            verify(attendeeAdapter, times(1)).select(eq(user));
        }
        onView(withId(R.id.expense_description)).check(hasText(expense.getDescription()));
        onView(withId(R.id.expense_amount)).check(hasText(String.valueOf(expense.getAmount() / 100.0)));
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
