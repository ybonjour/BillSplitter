package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.services.LoginService;
import ch.pantas.billsplitter.services.MigrationService;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.framework.CustomMatchers.matchesUser;
import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.closeSoftKeyboard;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginTest extends BaseEspressoTest<Login> {
    @Mock
    private SharedPreferenceService preferenceService;

    @Mock
    private ActivityStarter activityStarter;

    @Mock
    private UserService userService;

    @Mock
    private LoginService loginService;

    @Mock
    private EventService eventService;

    @Mock
    private MigrationService migrationService;

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
    public void testStartButtonIsShown() {
        // When
        getActivity();

        // Then
        onView(withId(R.id.action_login_start)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testUserIdIsNotLoggedIfNoUserNameEntered() {
        // Given
        getActivity();

        // When
        onView(withId(R.id.action_login_start)).perform(click());

        // Then
        verify(loginService, never()).login(any(User.class));

    }

    @LargeTest
    public void testNameFieldIsColoredRedIfNoUserNameIsEntered() {
        // Given
        getActivity();

        // When
        onView(withId(R.id.action_login_start)).perform(click());

        // Then
        onView(withId(R.id.user_name)).check(hasBackgroundColor(R.color.error_color));
    }

    @LargeTest
    public void testUserIsLoggedInWhenPressingStart() throws InterruptedException {
        // Given
        String userName = "Joe";
        getActivity();
        onView(withId(R.id.user_name)).perform(typeText(userName));
        closeSoftKeyboard();
        //HACK: Await keyboard closed, since this animation can not be disabled on the phone
        Thread.sleep(100);

        // When
        onView(withId(R.id.action_login_start)).perform(click());

        // Then
        verify(loginService, times(1)).login(argThat(matchesUser(userName)));
    }

    @LargeTest
    public void testStartEventIsStartedWhenPressingStart() throws InterruptedException {
        // Given
        String userName = "Joe";
        getActivity();
        onView(withId(R.id.user_name)).perform(typeText(userName));
        closeSoftKeyboard();
        //HACK: Await keyboard closed, since this animation can not be disabled on the phone
        Thread.sleep(100);

        // When
        onView(withId(R.id.action_login_start)).perform(click());

        // Then
        verify(activityStarter, times(1)).startStartEvent(any(Login.class));
    }

    @LargeTest
    public void testStartEventIsStartedWhenUserAlreadyLoggedInButNoActiveEventIsSet() {
        // Given
        when(userService.getMe()).thenReturn(new User("a", "Joe"));
        when(eventService.getActiveEvent()).thenReturn(null);

        // When
        getActivity();

        // Then
        verify(activityStarter, times(1)).startStartEvent(any(Context.class));
    }

    @LargeTest
    public void testEventDetailIsStartedWhenUserIsLoggedInAndActiveEventIsSet() {
        // Given
        when(userService.getMe()).thenReturn(new User("a", "joe"));
        Event event = mock(Event.class);
        when(eventService.getActiveEvent()).thenReturn(event);

        // When
        getActivity();

        // Then
        verify(activityStarter, times(1)).startEventDetails(any(Context.class), eq(event), eq(true));
    }
}
