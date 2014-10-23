package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.CURRENCY;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.NAME;
import static ch.pantas.billsplitter.model.SupportedCurrency.CHF;
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
        SupportedCurrency currency = CHF;
        Cursor cursor = createEventCursor(id, name, currency);

        // When
        Event event = mapper.map(cursor);

        // Then
        assertNotNull(event);
        assertEquals(id, event.getId());
        assertEquals(name, event.getName());
        assertEquals(currency, event.getCurrency());
    }

    @SmallTest
    public void testValuesThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            mapper.getValues(null);
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
        Event event = new Event(id, name, CHF);

        // When
        ContentValues values = mapper.getValues(event);

        // Then
        assertEquals(id, values.getAsString(ID));
        assertEquals(name, values.getAsString(NAME));
        assertEquals(CHF.toString(), values.getAsString(CURRENCY));
    }

    @SmallTest
    public void testValuesDoesNotReturnIdIfIdIsNull() {
        // Given
        String name = "Event 1";
        Event event = new Event(name, CHF);

        // When
        ContentValues values = mapper.getValues(event);

        // Then
        assertFalse(values.containsKey(ID));
    }

    private Cursor createEventCursor(String id, String name, SupportedCurrency currency) {
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getString(0)).thenReturn(id);
        when(c.getColumnIndex(NAME)).thenReturn(1);
        when(c.getString(1)).thenReturn(name);
        when(c.getColumnIndex(CURRENCY)).thenReturn(2);
        when(c.getString(2)).thenReturn(currency.toString());

        return c;
    }
}
