package ch.pantas.billsplitter.ui.fragment;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.apps.common.testing.ui.espresso.DataInteraction;
import com.google.inject.Inject;

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
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;

public class ParticipantsFragmentTest extends BaseEventDetailsFragmentTest {
    private static final User JOE = new User(randomUUID().toString(), "Joe");
    private static final User MARY = new User(randomUUID().toString(), "Mary");

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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(sharedPreferenceService.getUserId()).thenReturn(me.getId());

        // Given
        Participant participantMe = new Participant(me.getId(), event.getId());
        Participant participantJoe = new Participant(JOE.getId(), event.getId());
        Participant participantMary = new Participant(MARY.getId(), event.getId());
        when(participantStore.getParticipants(event.getId())).thenReturn(asList(participantMe, participantJoe));
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

    private DataInteraction onParticipant(User participant){
        return onData(Matchers.<Object>equalTo(participant)).inAdapterView(withId(R.id.participant_grid));
    }

    private DataInteraction onNonParticipant(User nonparticipant){
        return onData(Matchers.<Object>equalTo(nonparticipant)).inAdapterView(withId(R.id.user_grid));
    }
}
