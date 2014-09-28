package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventRowMapperTest extends BaseMockitoInstrumentationTest {

    @Inject
    private EventRowMapper mapper;

    @SmallTest
    public void testMapThrowsNullPointerExceptionIfNoCursorProvided() {
        try {
            mapper.map(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testMapCorrectlyMapsCursor() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Event 1";
        Cursor cursor = createEventCursor(id, name);

        // When
        Event event = mapper.map(cursor);

        // Then
        assertNotNull(event);
        assertEquals(id, event.getId());
        assertEquals(name, event.getName());
    }

    @SmallTest
    public void testValuesThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            mapper.values(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testValuesReturnsCorrectValues() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Event 1";
        Event event = new Event(id, name);

        // When
        ContentValues values = mapper.values(event);

        // Then
        assertEquals(id, values.getAsString(ID));
        assertEquals(name, values.getAsString(NAME));
    }

    @SmallTest
    public void testValuesDoesNotReturnIdIfIdIsNull() {
        // Given
        String name = "Event 1";
        Event event = new Event(name);

        // When
        ContentValues values = mapper.values(event);

        // Then
        assertFalse(values.containsKey(ID));
    }

    private Cursor createEventCursor(String id, String name) {
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getString(0)).thenReturn(id);
        when(c.getColumnIndex(NAME)).thenReturn(1);
        when(c.getString(1)).thenReturn(name);

        return c;
    }
}
