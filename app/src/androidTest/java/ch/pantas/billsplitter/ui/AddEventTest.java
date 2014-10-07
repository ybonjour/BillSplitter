package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.framework.CustomViewAssertions.hasBackgroundColor;
import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AddEventTest extends BaseEspressoTest<AddEvent> {

    @Mock
    private EventStore eventStore;

    @Mock
    private ActivityStarter activityStarter;

    public AddEventTest() {
        super(AddEvent.class);
    }

    @LargeTest
    public void testCorrectTitleIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testNextButtonIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.next)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testEventIsAddedIfNextButtonIsPressed() {
        // Given
        String eventName = "An Event";
        getActivity();
        onView(withId(R.id.event_name)).perform(typeText(eventName));

        // When
        onView(withText(R.string.next)).perform(click());

        // Then
        verify(eventStore, times(1)).persist(argThat(newEventWith(eventName)));
    }

    @LargeTest
    public void testAddParticipantsIsStartedIfNextButtonIsPressed() {
        // Given
        String eventName = "An Event";
        getActivity();
        onView(withId(R.id.event_name)).perform(typeText(eventName));

        // When
        onView(withText(R.string.next)).perform(click());

        // Then
        verify(activityStarter, times(1)).startAddParticipants(any(Context.class), any(Event.class));
    }

    @LargeTest
    public void testEventIsNotAddedIfNextButtonIsPressedWithEmptyData() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.next)).perform(click());

        // Then
        verifyZeroInteractions(eventStore);
        onView(withId(R.id.event_name)).check(hasBackgroundColor(R.color.error_color));
    }

    @LargeTest
    public void testEditEventCurrentEventNameDisplayed() {
        // Given
        Event event = new Event("abc", "testname");
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);

        // When
        getActivity();

        // Then
        onView(withId(R.id.event_name)).check(matches(withText(event.getName())));

    }

    @LargeTest
    public void testEditParticipantsIsStartedIfNextButtonIsPressed() {
        // Given
        Event event = new Event("abc", "testname");
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);

        // When
        getActivity();
        onView(withText(R.string.next)).perform(click());

        // Then
        verify(eventStore, times(1)).persist(eq(event));
        verify(activityStarter, times(1)).startAddParticipants(any(Context.class), eq(event));
    }

    private static Matcher<Event> newEventWith(final String eventName) {
        return new TypeSafeMatcher<Event>() {
            @Override
            public boolean matchesSafely(Event event) {
                return event.isNew() && eventName.equals(event.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New event with name ");
                description.appendText(eventName);
            }
        };
    }

}
