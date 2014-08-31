package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.model.Event;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class EventStoreTest extends InstrumentationTestCase {
    @Inject
    private EventStore store;

    @Mock
    private BillSplitterDatabase database;

    @Mock
    private EventRowMapper mapper;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Cursor cursor;

    @Mock
    private Event event;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        when(database.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(sqLiteDatabase.rawQuery(anyString(), any(String[].class))).thenReturn(cursor);
    }

    @SmallTest
    public void testGetAllEventsReturnsCorrectEventWithOneEvent() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(event);

        // When
        List<Event> events = store.getAllEvents();

        // Then
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }
}
