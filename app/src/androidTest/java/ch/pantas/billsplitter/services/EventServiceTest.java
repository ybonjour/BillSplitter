package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.model.SupportedCurrency.CHF;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
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
    private UserService userService;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        event = new Event(randomUUID(), "Event name", CHF, randomUUID());
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
            Event event = new Event("Name", CHF, randomUUID());
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
        Event newActiveEvent = new Event(randomUUID(), "Name", CHF, randomUUID());
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
        verify(sharedPreferenceService, never()).storeActiveEventId(any(UUID.class));
        assertNull(result);
    }

    @SmallTest
    public void testRemoveEventDoesNotSetNewActiveEventWhenActiveEventWasNotProvidedEvent(){
        // Given
        Event activeEvent = new Event(randomUUID(), "Name", CHF, randomUUID());
        when(sharedPreferenceService.getActiveEventId()).thenReturn(activeEvent.getId());
        when(eventStore.getById(activeEvent.getId())).thenReturn(activeEvent);

        // When
        Event result = eventService.removeEventAndGetActiveEvent(event);

        // Then
        verify(sharedPreferenceService, never()).storeActiveEventId(any(UUID.class));
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

    @SmallTest
    public void testGetActiveEventReturnsNullIfNoActiveEventIsStored(){
        // Given
        when(sharedPreferenceService.getActiveEventId()).thenReturn(null);

        // When
        Event event = eventService.getActiveEvent();

        // Then
        assertNull(event);
    }

    @SmallTest
    public void testGetActiveEventReturnsNullIfEventForStoredEventIdDoesNotExist(){
        // Given
        UUID eventId = randomUUID();
        when(sharedPreferenceService.getActiveEventId()).thenReturn(eventId);
        when(eventStore.getById(event.getId())).thenReturn(null);

        // When
        Event event = eventService.getActiveEvent();

        // Then
        assertNull(event);
    }

    @SmallTest
    public void testGetActiveEventReturnsStoredEvent(){
        // Given
        Event event = new Event(randomUUID(), "An event", CHF, randomUUID());
        when(sharedPreferenceService.getActiveEventId()).thenReturn(event.getId());
        when(eventStore.getById(event.getId())).thenReturn(event);

        // When
        Event result = eventService.getActiveEvent();

        // Then
        assertEquals(result, event);
    }

    @SmallTest
    public void testCreateEventThrowsNullPointerExceptionIfNoNameProvided(){
        try {
            eventService.createEvent(null, CHF);
            fail("No exception has been thrown");
        } catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCreateEventThrowsIllegalArgumentExceptionIfEmptyNameProvided(){
        try {
            eventService.createEvent("", CHF);
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCreateEventCreatesEventCorrectly(){
        // Given
        String name = "An event";
        SupportedCurrency currency = CHF;
        User me = new User(randomUUID(), "Joe");
        when(userService.getMe()).thenReturn(me);

        // When
        Event event = eventService.createEvent(name, currency);

        // Then
        assertNotNull(event);
        assertEquals(name, event.getName());
        assertEquals(currency, event.getCurrency());
        assertEquals(me.getId(), event.getOwnerId());
        verify(eventStore, times(1)).persist(event);
    }
}
