package ch.pantas.billsplitter.ui;

import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.splitty.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StartEventTest extends BaseEspressoTest<StartEvent> {
    public StartEventTest() {
        super(StartEvent.class);
    }

    @Mock
    private ActivityStarter activityStarter;

    @LargeTest
    public void testEventListHasCorrectTitle() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.events_title)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testAddButtonIsShown() {
        // When
        getActivity();

        // Then
        onView(withId(R.id.action_add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testAddEventActivityIsStartedWhenAddButtonIsClicked() {
        // Given
        getActivity();

        // When
        onView(withId(R.id.action_add_event)).perform(click());

        // Then
        verify(activityStarter, times(1)).startAddEvent(any(StartEvent.class));
    }

    @LargeTest
    public void testHelpTextIsShown() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event_help_text)).check(matches(isDisplayed()));
    }
}
