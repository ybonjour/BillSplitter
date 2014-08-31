package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import junit.framework.TestCase;

import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase.EventTable.NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventRowMapperTest extends TestCase {

    @Inject
    private EventRowMapper mapper;

    @SmallTest
    public void testMapCorrectlyMapsCursor() {
        // Given
        int id = 1;
        String name = "Event 1";
        Cursor cursor = createEventCursor(id, name);

        // When
        Event event = mapper.map(cursor);

        // Then
        assertNotNull(event);
        assertEquals(id, event.getId());
        assertEquals(name, event.getName());
    }

    private Cursor createEventCursor(int id, String name) {
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getInt(0)).thenReturn(id);
        when(c.getColumnIndex(NAME)).thenReturn(1);
        when(c.getString(1)).thenReturn(name);

        return c;
    }
}
