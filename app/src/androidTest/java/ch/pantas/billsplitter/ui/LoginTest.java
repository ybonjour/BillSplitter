package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class LoginTest extends BaseEspressoTest<Login> {
    @Mock
    private UserStore userStore;

    @Mock
    private SharedPreferenceService preferenceService;

    @Mock
    private ActivityStarter activityStarter;

    public LoginTest() {
        super(Login.class);
    }

    @LargeTest
    public void testCorrectTitleIsDisplayed(){
        // When
        getActivity();

        // Then
        onView(withText(R.string.set_user_name)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testCorrectDescriptionIsDisplayed(){
        // When
        getActivity();

        // Then
        onView(withText(R.string.set_user_name_description));
    }

    @LargeTest
    public void testSaveButtonIsShown(){
        // When
        getActivity();

        // Then
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testUserNameIsNotStoredIfNoUserNameEntered(){
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
    public void testUserNameIsWrittenToSharedPreferencesWhenSaved(){
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
    public void testEventListIsStartedWhenUsernameIsAlreadySet(){
        // Given
        when(preferenceService.getUserName()).thenReturn("Joe");

        // When
        getActivity();

        // Then
        verify(activityStarter, times(1)).startEventList(any(Context.class));
    }

}
