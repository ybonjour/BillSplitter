package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginTest extends BaseEspressoTest<Login> {
    @Mock
    private UserStore userStore;

    @Mock
    private SharedPreferenceService preferenceService;

    @Mock
    private ActivityStarter activityStarter;

    @Mock
    private EventStore eventStore;

    @Mock
    private UserService userService;

    public LoginTest() {
        super(Login.class);
    }

    @LargeTest
    public void testCorrectTitleIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testSaveButtonIsShown() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testUserNameIsNotStoredIfNoUserNameEntered() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(preferenceService, never()).storeUserName(anyString());
    }

    @LargeTest
    public void testNameFieldIsColoredRedIfNoUserNameIsEntered() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        onView(withId(R.id.user_name)).check(hasBackgroundColor(R.color.error_color));
    }

    @LargeTest
    public void testUserNameIsWrittenToSharedPreferencesWhenSaved() {
        // Given
        String userName = "Joe";
        getActivity();
        onView(withId(R.id.user_name)).perform(typeText(userName));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(preferenceService, times(1)).storeUserName(userName);

    }

    @LargeTest
    public void testEventListIsStartedWhenUsernameIsAlreadySet() {
        // Given
        when(userService.getMe()).thenReturn(new User("a", "Joe"));

        // When
        getActivity();

        // Then
        verify(activityStarter, times(1)).startStartEvent(any(Context.class));
    }

    @LargeTest
    public void testEventListIsStartedWhenNoEventIdIsAlreadySet() {
        // Given
        when(userService.getMe()).thenReturn(new User("a", "joe"));
        when(preferenceService.getActiveEventId()).thenReturn(null);

        // When
        getActivity();

        // Then
        verify(activityStarter, times(1)).startStartEvent(any(Context.class));
    }

    @LargeTest
    public void testEventDetailIsStartedWhenEventIdIsAlreadySet() {
        // Given
        when(userService.getMe()).thenReturn(new User("a", "joe"));
        Event event = new Event("eventId", "eventName", EUR, randomUUID().toString());
        when(preferenceService.getActiveEventId()).thenReturn(event.getId());
        when(eventStore.getById(event.getId())).thenReturn(event);

        // When
        getActivity();

        // Then
        verify(activityStarter, times(1)).startEventDetails(any(Context.class), eq(event), eq(true));
    }
}
