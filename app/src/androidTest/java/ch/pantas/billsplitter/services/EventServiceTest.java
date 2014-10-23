package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.LinkedList;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.model.SupportedCurrency.CHF;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventServiceTest extends BaseMockitoInstrumentationTest {

    @Inject
    private EventService eventService;

    @Mock
    private EventStore eventStore;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        event = new Event(randomUUID().toString(), "Event name", CHF);
        when(sharedPreferenceService.getActiveEventId()).thenReturn(null);
    }

    @SmallTest
    public void testRemoveEventThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            eventService.removeEventAndGetActiveEvent(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveEventThrowsIllegalArgumentExceptionIfNewEventProvided() {
        try {
            Event event = new Event("Name", CHF);
            eventService.removeEventAndGetActiveEvent(event);
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveEventRemovesEvent() {
        // When
        eventService.removeEventAndGetActiveEvent(event);

        // Then
        verify(eventStore, times(1)).removeById(event.getId());
    }

    @SmallTest
    public void testRemoveEventSetsNewActiveEventAndReturnsIt(){
        // Given
        Event newActiveEvent = new Event(randomUUID().toString(), "Name", CHF);
        when(sharedPreferenceService.getActiveEventId()).thenReturn(event.getId());
        when(eventStore.getAll()).thenReturn(asList(newActiveEvent));

        // When
        Event result = eventService.removeEventAndGetActiveEvent(event);

        // Then
        verify(sharedPreferenceService, times(1)).storeActiveEventId(newActiveEvent.getId());
        assertEquals(newActiveEvent, result);
    }

    @SmallTest
    public void testRemoveEventDoesNotSetNewActiveEventWhenNoActiveEventWasSetBefore(){
        // When
        Event result = eventService.removeEventAndGetActiveEvent(event);

        // Then
        verify(sharedPreferenceService, never()).storeActiveEventId(anyString());
        assertNull(result);
    }

    @SmallTest
    public void testRemoveEventDoesNotSetNewActiveEventWhenActiveEventWasNotProvidedEvent(){
        // Given
        Event activeEvent = new Event(randomUUID().toString(), "Name", CHF);
        when(sharedPreferenceService.getActiveEventId()).thenReturn(activeEvent.getId());
        when(eventStore.getById(activeEvent.getId())).thenReturn(activeEvent);

        // When
        Event result = eventService.removeEventAndGetActiveEvent(event);

        // Then
        verify(sharedPreferenceService, never()).storeActiveEventId(anyString());
        assertEquals(activeEvent, result);
    }

    @SmallTest
    public void testRemoveEventDoesClearActiveEventIfNoEventLeft(){
        // Given
        when(sharedPreferenceService.getActiveEventId()).thenReturn(event.getId());
        when(eventStore.getAll()).thenReturn(new LinkedList<Event>());

        // When
        Event result = eventService.removeEventAndGetActiveEvent(event);

        // Then
        verify(sharedPreferenceService, times(1)).storeActiveEventId(null);
        assertNull(result);
    }
}
