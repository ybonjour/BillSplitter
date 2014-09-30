package ch.pantas.billsplitter.ui;

import android.graphics.drawable.ColorDrawable;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.yvu.myapplication.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AddEventTest extends BaseEspressoTest<AddEvent> {

    public AddEventTest() {
        super(AddEvent.class);
    }

    @Mock
    private EventStore eventStore;

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
    public void testEventIsAddedIfSaveButtonIsPressed() {
        // Given
        String eventName = "An Event";
        getActivity();
        onView(withId(R.id.event_name)).perform(typeText(eventName));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(eventStore, times(1)).persist(argThat(newEventWithName(eventName)));
    }

    @LargeTest
    public void testEventIsNotAddedIfSaveButtonIsPressedWithEmptyData() {
        // Given
        String eventName = "";
        getActivity();
        int color = getActivity().getResources().getColor(R.color.error_color);
        onView(withId(R.id.event_name)).perform(typeText(eventName));

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        // No Event is persisted
        verify(eventStore, times(0)).persist(argThat(newEventWithName(eventName)));
        // EditText background is red
        assertEquals(((ColorDrawable)getActivity().findViewById(R.id.event_name).getBackground()).getColor(), color);
        // Add expense activity is still shown
        onView(withId(R.id.event_name)).check(matches(isDisplayed()));
    }

    private static Matcher<Event> newEventWithName(final String eventName) {
        return new TypeSafeMatcher<Event>() {
            @Override
            public boolean matchesSafely(Event event) {
                return event.isNew() && eventName.equals(event.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("new event with name ");
                description.appendText(eventName);
            }
        };
    }
}
