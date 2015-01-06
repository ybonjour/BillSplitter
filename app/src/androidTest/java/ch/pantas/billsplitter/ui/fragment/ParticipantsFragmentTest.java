package ch.pantas.billsplitter.ui.fragment;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.apps.common.testing.ui.espresso.DataInteraction;

import org.hamcrest.Matchers;
import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.ExpensePresentation;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ExpenseService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.ParticipantManager;
import ch.pantas.splitty.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.text.StringStartsWith.startsWith;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParticipantsFragmentTest extends BaseEventDetailsFragmentTest {
    private static final User JOE = new User(randomUUID(), "Joe");
    private static final User MARY = new User(randomUUID(), "Mary");

    @Mock
    private UserStore userStore;

    @Mock
    private AttendeeStore attendeeStore;

    //@Mock
    //private ParticipantManager participantManager;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private ExpenseService expenseService;

    Participant participantJoe;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(sharedPreferenceService.getUserId()).thenReturn(me.getId());

        // Given
        Participant participantMe = new Participant(randomUUID(), me.getId(), event.getId(), true, 0);
        participantJoe = new Participant(randomUUID(), JOE.getId(), event.getId(), true, 0);
        when(participantStore.getParticipants(event.getId())).thenReturn(asList(participantMe, participantJoe));
        when(participantStore.getParticipant(event.getId(), me.getId())).thenReturn(participantMe);
        when(participantStore.getParticipant(event.getId(), JOE.getId())).thenReturn(participantJoe);

        when(userStore.getById(me.getId())).thenReturn(me);
        when(userStore.getById(JOE.getId())).thenReturn(JOE);
        when(userStore.getById(MARY.getId())).thenReturn(MARY);

        when(userStore.getAll()).thenReturn(asList(me, JOE, MARY));

        // When
        getActivity();
        onView(withId(R.id.event_details_content)).perform(swipeLeft(), swipeLeft());
    }

    @SmallTest
     public void testCorrectParticipantsAreShown() {
        // Then
        onParticipant(me).check(matches(isDisplayed()));
        onParticipant(JOE).check(matches(isDisplayed()));
        onNonParticipant(MARY).check(matches(isDisplayed()));
    }

    @SmallTest
    public void testAddNonParticipantToParticipants() {
        // When
        onNonParticipant(MARY).perform(click());

        // Then
        onParticipant(MARY).check(matches(isDisplayed()));
        verify(participantStore, times(1)).persist(eq(new Participant(MARY.getId(), event.getId())));
    }

    @SmallTest
    public void testRemoveParticipant() {
        // When
        onParticipant(JOE).perform(click());

        // Then
        onNonParticipant(JOE).check(matches(isDisplayed()));
        verify(participantStore, times(1)).removeBy(eq(event.getId()), eq(JOE.getId()));
    }

    @SmallTest
    public void testRemovingParticipantInExpenseNotPossible() {
        // When
        when(attendeeStore.getAttendingParticipants(expense.getId())).thenReturn(asList(participantJoe));
        onParticipant(JOE).perform(click());

        // Then
        onParticipant(JOE).check(matches(isDisplayed()));
        verify(participantStore, times(0)).removeBy(eq(event.getId()), eq(JOE.getId()));
    }

    @SmallTest
    public void testCantRemoveMe() {
        // When
        when(attendeeStore.getAttendingParticipants(expense.getId())).thenReturn(asList(participantJoe));
        onParticipant(me).perform(click());

        // Then
        onParticipant(me).check(matches(isDisplayed()));
        verify(participantStore, times(0)).removeBy(eq(event.getId()), eq(me.getId()));
    }

    private DataInteraction onParticipant(User participant){
        return onData(Matchers.<Object>equalTo(participant)).inAdapterView(withId(R.id.participant_grid));
    }

    private DataInteraction onNonParticipant(User nonparticipant){
        return onData(Matchers.<Object>equalTo(nonparticipant)).inAdapterView(withId(R.id.user_grid));
    }
}
