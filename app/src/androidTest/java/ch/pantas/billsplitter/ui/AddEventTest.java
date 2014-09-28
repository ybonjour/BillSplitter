package ch.pantas.billsplitter.ui;

import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
    public void testCorrectTitleIsShown() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testSaveButtonIsShown() {
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
        verify(eventStore, times(1)).persist(argThat(new EventMatcher(true, eventName)));
    }

    private class EventMatcher extends BaseMatcher<Event> {

        private final boolean isNew;
        private final String eventName;

        private EventMatcher(boolean isNew, String eventName) {
            this.isNew = isNew;
            this.eventName = eventName;
        }

        @Override
        public boolean matches(Object o) {
            if (!(o instanceof Event)) return false;

            Event event = (Event) o;

            return event.isNew() == isNew && eventName.equals(event.getName());
        }

        @Override
        public void describeTo(Description description) {

        }
    }
}
