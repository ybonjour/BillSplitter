package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static ch.pantas.billsplitter.ui.AddParticipants.ARGUMENT_EXPENSE_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddParticipantsTest extends BaseEspressoTest<AddParticipants> {
    @Mock
    private ParticipantStore participantStore;

    @Mock
    private UserStore userStore;

    @Mock
    private ExpenseStore expenseStore;

    private Expense expense;
    private User user;

    public AddParticipantsTest() {
        super(AddParticipants.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        expense = new Expense("id", "eventId", "payerId", "description", 10.0);

        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EXPENSE_ID, expense.getId());
        setActivityIntent(intent);

        when(expenseStore.getById(expense.getId())).thenReturn(expense);

        user = new User("id", "userName");
        when(participantStore.getParticipants(expense.getId())).thenReturn(asList(user));
    }

    @LargeTest
    public void testExpenseDescriptionIsShown() {
        // When
        getActivity();

        // Then
        onView(withText(expense.getDescription())).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testParticipantsAreShown() {
        // When
        getActivity();

        // Then
        onData(anything()).atPosition(0).check(matches(withText(user.getName())));
    }

    @LargeTest
    public void testAddUserUsesExistingUserIfExists() {
        // Given
        when(userStore.getUserWithName(user.getName())).thenReturn(user);
        getActivity();
        onView(withId(R.id.participant_name)).perform(typeText(user.getName()));

        // When
        onView(withText(R.string.add)).perform(click());

        // Then
        verify(userStore, never()).persist(any(User.class));
        verify(participantStore, times(1)).persist(argThat(participantWith(expense.getId(), user.getId())));
    }

    @LargeTest
    public void testAddUserCreatesNewUserIfNotExists() {
        // Given
        String newUsername = "Joe";
        final String newUserId = "newUserId";
        when(userStore.getUserWithName(newUsername)).thenReturn(null);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                user.setId(newUserId);
                return null;
            }
        }).when(userStore).persist(any(User.class));
        getActivity();
        onView(withId(R.id.participant_name)).perform(typeText(newUsername));

        // When
        onView(withText(R.string.add)).perform(click());

        // Then
        verify(participantStore, times(1)).persist(argThat(participantWith(expense.getId(), newUserId)));
    }

    @LargeTest
    public void testParticipantNotPersistedIfNoNameEntered() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.add)).perform(click());

        // Then
        verify(participantStore, never()).persist(any(Participant.class));
    }

    @LargeTest
    public void testNameFieldColoredRedIfNoNameEntered() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.add)).perform(click());

        // Then
        onView(withId(R.id.participant_name)).check(hasBackgroundColor(R.color.error_color));
    }

    private static Matcher<Participant> participantWith(final String expenseId, final String userId) {
        return new TypeSafeMatcher<Participant>() {
            @Override
            public boolean matchesSafely(Participant participant) {
                return expenseId.equals(participant.getExpense()) && userId.equals(participant.getUser());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Participant with Expense ");
                description.appendText(expenseId);
                description.appendText(" and User ");
                description.appendText(userId);
            }
        };
    }
}
