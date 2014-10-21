package ch.pantas.billsplitter.ui;

import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.model.Currency.EUR;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StartEventTest extends BaseEspressoTest<StartEvent> {
    public StartEventTest() {
        super(StartEvent.class);
    }

    @Mock
    private EventStore store;

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
    public void testListItemIsShownCorrectly() {
        // Given
        String eventName = "Event 1";
        String uuid = UUID.randomUUID().toString();
        List<Event> events = new ArrayList<Event>();
        events.add(new Event(uuid, eventName, EUR));
        when(store.getAll()).thenReturn(events);

        // When
        getActivity();

        // Then
        onData(anything()).atPosition(0).check(matches(withText(eventName)));
    }

    @LargeTest
    public void testExpensesListActivityIsStartedWhenClickingOnEvent() {
        // Given
        Event event = new Event("12345", "Event 1", EUR);
        List<Event> events = new ArrayList<Event>();
        events.add(event);
        when(store.getAll()).thenReturn(events);
        getActivity();

        // When
        onData(anything()).atPosition(0).perform(click());

        // Then
        verify(activityStarter, times(1)).startEventDetails(any(StartEvent.class), eq(event));
    }

    @LargeTest
    public void testAddButtonIsShown() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testAddEventActivityIsStartedWhenAddButtonIsClicked() {
        // Given
        getActivity();

        // When
        onView(withText(R.string.add_event)).perform(click());

        // Then
        verify(activityStarter, times(1)).startAddEvent(any(StartEvent.class));
    }
}
