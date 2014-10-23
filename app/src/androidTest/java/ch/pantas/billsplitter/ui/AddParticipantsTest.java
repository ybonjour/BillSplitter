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
import ch.pantas.billsplitter.ui.adapter.UserAdapter;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static ch.pantas.billsplitter.ui.AddParticipants.EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Mock
    UserAdapter participantAdapter;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event("abcd", "An Event", EUR);
        Intent intent = new Intent();
        intent.putExtra(EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);

        when(participantAdapter.getViewTypeCount()).thenReturn(1);
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
        // Given
        List<User> participantsList = new LinkedList<User>();
        participantsList.add(new User("a", "Me"));
        participantsList.add(new User("b", "Hans"));
        participantsList.add(new User("c", "Fritz"));

        List<User> otherParticipantsList = new LinkedList<User>(participantsList);
        otherParticipantsList.remove(0);

        when(participantStore.getParticipants(event.getId())).thenReturn(participantsList);
        when(participantManager.getParticipants()).thenReturn(participantsList);
        when(sharedPreferenceService.getUserName()).thenReturn("Me");
        when(participantManager.filterOutParticipants(anyList())).thenReturn(otherParticipantsList);

        // When
        getActivity();

        // Then
        verify(participantAdapter, times(2)).setUsers(participantsList);
    }

    // TODO: Participants UI tests
}
