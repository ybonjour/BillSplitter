package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Matchers;
import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.framework.CustomMatchers.matchesParticipant;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static ch.pantas.billsplitter.ui.AddParticipants.EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
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
    private UserService userService;

    @Mock
    private ParticipantStore participantStore;

    @Mock
    private ParticipantManager participantManager;

    private Event event;
    private User me;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event("abcd", "An Event", EUR, randomUUID().toString());
        Intent intent = new Intent();
        intent.putExtra(EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);

        me = new User(randomUUID().toString(), "Dave");
        when(userService.getMe()).thenReturn(me);

        // This is needed that when Add Participant finishes EventDetails can
        // be started correctly
        when(eventStore.getAll()).thenReturn(asList(event));
    }

    @LargeTest
    public void testCorrectTitleIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testOnStartUpNormalModeIsDisplayed() {
        // When
        getActivity();

        // Then
        verifyNormalMode();
    }

    @LargeTest
    public void testWhenStartingToTypeNameSearchModeIsDisplayed() {
        // Given
        getActivity();

        // When
        onView(withId(R.id.user_name)).perform(typeText("J"));

        // Then
        verifySearchMode();
    }

    @LargeTest
    public void testClickingOnCancelInSearchModeDisablesSearchMode() {
        // Given
        getActivity();
        onView(withId(R.id.user_name)).perform(typeText("J"));

        // When
        onView(withId(R.id.cancel_searchmode)).perform(click());

        // Then
        verifyNormalMode();
    }

    @LargeTest
    public void testClickingOnCancelDoesNotAddParticipant() {
        // Given
        getActivity();
        onView(withId(R.id.user_name)).perform(typeText("J"));

        // When
        onView(withId(R.id.cancel_searchmode)).perform(click());

        // Then
        verify(participantManager, never()).addParticipant(any(User.class));
    }

    @LargeTest
    public void testMeIsAlwaysAddedAsFixedParticipant() {
        // Given
        User me = new User(randomUUID().toString(), "Joe");
        when(userService.getMe()).thenReturn(me);

        // When
        getActivity();

        // Then
        verify(participantManager, times(1)).addFixedParticipant(me);
    }

    @LargeTest
    public void testWhenNoUsernameIseEnteredAllNonParticipatingUsersAreShown() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        List<User> users = asList(user);
        when(userStore.getAll()).thenReturn(users);
        when(participantManager.filterOutParticipants(users)).thenReturn(users);

        // When
        getActivity();

        // Then
        onData(Matchers.<Object>equalTo(user)).inAdapterView(withId(R.id.user_grid)).check(matches(isDisplayed()));
        verify(userStore, times(1)).getAll();
        verify(participantManager, times(1)).filterOutParticipants(users);
    }

    @LargeTest
    public void testClickingOnUserAddsHimAsParticipant() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        List<User> users = asList(user);
        when(userStore.getAll()).thenReturn(users);
        when(participantManager.filterOutParticipants(users)).thenReturn(users);
        getActivity();

        // When
        onData(Matchers.<Object>equalTo(user)).inAdapterView(withId(R.id.user_grid)).perform(click());

        // Then
        verify(participantManager, times(1)).addParticipant(user);
    }

    @LargeTest
    public void testParticipatingUsersAreShownCorrectly() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(participantManager.getParticipants()).thenReturn(asList(user));

        // When
        getActivity();

        // Then
        onData(Matchers.<Object>equalTo(user)).inAdapterView(withId(R.id.participant_grid)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testClickingOnParticipantRemovesItFromParticipants() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(participantManager.getParticipants()).thenReturn(asList(user));
        getActivity();

        // When
        onData(Matchers.<Object>equalTo(user)).inAdapterView(withId(R.id.participant_grid)).perform(click());

        // Then
        verify(participantManager, times(1)).removeParticipant(user);
    }

    @LargeTest
    public void testSaveRemovesAllParticipantsAndAddsSelectedOnes() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(participantManager.getParticipants()).thenReturn(asList(user));
        getActivity();

        // When
        onView(withId(R.id.action_save_event)).perform(click());

        // Then
        verify(participantStore, times(1)).removeAll(event.getId());
        verify(participantStore, times(1)).persist(argThat(matchesParticipant(user.getId(),
                event.getId(), false, 0)));
    }

    @LargeTest
    public void testSaveAddsMeAsConfirmedParticipant() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(participantManager.getParticipants()).thenReturn(asList(user));
        when(userService.getMe()).thenReturn(user);
        getActivity();

        // When
        onView(withId(R.id.action_save_event)).perform(click());

        // Then
        verify(participantStore, times(1)).persist(argThat(matchesParticipant(user.getId(),
                event.getId(), true, 0)));
    }


    private void verifyNormalMode() {
        onView(withId(R.id.action_save_event)).check(matches(isDisplayed()));
        onView(withId(R.id.participant_container)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_searchmode)).check(matches(not(isDisplayed())));
    }

    private void verifySearchMode() {
        onView(withId(R.id.action_save_event)).check(doesNotExist());
        onView(withId(R.id.participant_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.cancel_searchmode)).check(matches(isDisplayed()));
    }
}
