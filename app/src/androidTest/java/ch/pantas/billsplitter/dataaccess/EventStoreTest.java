package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

import static org.mockito.Mockito.when;

public class EventStoreTest extends BaseStoreTest {
    @Inject
    private EventStore store;

    @Mock
    private EventRowMapper mapper;

    @Mock
    private Event event;

    @SmallTest
    public void testGetAllEventsReturnsEmptyListWithZeroEvents() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<Event> events = store.getAll();

        // Then
        assertEquals(0, events.size());
    }

    @SmallTest
    public void testGetAllEventsReturnsCorrectEventWithOneEvent() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(event);

        // When
        List<Event> events = store.getAll();

        // Then
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }
}
