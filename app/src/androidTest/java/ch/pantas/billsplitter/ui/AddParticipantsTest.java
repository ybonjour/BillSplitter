package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class AddParticipantsTest extends BaseEspressoTest<AddParticipants> {

    public AddParticipantsTest() {
        super(AddParticipants.class);
    }

    @Mock
    private EventStore eventStore;

    @Mock
    private UserStore userStore;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private ParticipantManager participantManager;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event("abcd", "An Event");
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);
    }

    @LargeTest
    public void testCorrectTitleIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testSaveButtonIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testEditParticipantsExistingParticipantsAreShown() {
        // TODO: FIXME (NullPointerException)
        // Given
        List<User> participantsList = new LinkedList<User>();
        participantsList.add(new User("a", "Me"));
        participantsList.add(new User("b", "Hans"));
        participantsList.add(new User("c", "Fritz"));
        when(participantManager.getParticipants()).thenReturn(participantsList);
        when(sharedPreferenceService.getUserName()).thenReturn("Me");
        // When
        getActivity();

        // Then
        onView(withText("Me")).check(matches(isDisplayed()));
        onView(withText("Hans")).check(matches(isDisplayed()));
        onView(withText("Fritz")).check(matches(isDisplayed()));
    }

    // TODO: Participants UI tests
}
