package ch.pantas.billsplitter.dataaccess;

import android.content.ContentValues;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenericStoreTest extends BaseStoreTest {
    @Inject
    private EventStore store;

    @Mock
    private EventRowMapper mapper;

    @Mock
    private Event event;

    @SmallTest
    public void testGetAllReturnsEmptyListWithZeroModels() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<Event> events = store.getAll();

        // Then
        assertEquals(0, events.size());
    }

    @SmallTest
    public void testGetAllReturnsCorrectModelWithOneModel() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(event);

        // When
        List<Event> events = store.getAll();

        // Then
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @SmallTest
    public void testGetByIdReturnsNullIfModelDoesNotExist() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        String id = "abc";

        // When
        Event event = store.getById(id);

        // Then
        assertNull(event);
    }

    @SmallTest
    public void testGetByIdReturnsCorrectModelIfItExists() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(event);
        String id = "abc";

        // When
        Event result = store.getById(id);

        // Then
        assertNotNull(result);
        assertEquals(event, result);
    }

    @SmallTest
    public void testPersistWithNewModel() {
        // Given
        final Event event = new Event("Lissabon Trip", EUR, "owner");

        // When
        store.persist(event);

        // Then
        assertNotNull(event.getId());
        verify(database, times(1)).insert(anyString(), any(ContentValues.class));
    }

    @SmallTest
    public void testPersistWithExistingModel() {
        // Given
        String id = "abc";
        Event event = new Event(id, "Lissabon Trip", EUR, "owner");

        // When
        store.persist(event);

        // Then
        assertEquals(id, event.getId());
        verify(database, times(1)).update(anyString(), any(ContentValues.class));
    }
}
