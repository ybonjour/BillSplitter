package ch.pantas.billsplitter;

import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.ui.EventList;
import ch.yvu.myapplication.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.mockito.Mockito.when;

public class EventListTest extends BaseEspressoTest<EventListTest, EventList> {
    public EventListTest() {
        super(EventList.class);
    }

    @Mock
    private EventStore store;

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
        List<Event> events = new ArrayList<Event>();
        events.add(new Event(1, eventName));
        when(store.getAllEvents()).thenReturn(events);

        // When
        getActivity();

        // Then
        onData(anything()).atPosition(0).check(matches(withText(eventName)));
    }


    @Override
    protected EventListTest getInstance() {
        return this;
    }
}
