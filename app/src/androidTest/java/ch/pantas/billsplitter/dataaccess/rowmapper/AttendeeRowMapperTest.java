package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Attendee;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.PARTICIPANT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AttendeeRowMapperTest extends BaseMockitoInstrumentationTest {

    @Inject
    private AttendeeRowMapper mapper;

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
        String expense = UUID.randomUUID().toString();
        String participant = UUID.randomUUID().toString();
        Cursor c = createAttendeeCursor(id, expense, participant);

        // When
        Attendee attendee = mapper.map(c);

        // Then
        assertNotNull(attendee);
        assertEquals(id, attendee.getId().toString());
        assertEquals(expense, attendee.getExpense().toString());
        assertEquals(participant, attendee.getParticipant().toString());
    }

    @SmallTest
    public void testGetValuesThrowsExceptionIfNoAttendeeProvided() {
        try {
            mapper.getValues(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetValuesReturnsCorrectValues() {
        // Given
        UUID id = UUID.randomUUID();
        UUID expense = UUID.randomUUID();
        UUID participant = UUID.randomUUID();
        Attendee attendee = new Attendee(id, expense, participant);

        // When
        ContentValues values = mapper.getValues(attendee);

        // Then
        assertNotNull(values);
        assertEquals(3, values.size());
        assertEquals(id.toString(), values.get(ID));
        assertEquals(expense.toString(), values.get(EXPENSE));
        assertEquals(participant.toString(), values.get(PARTICIPANT));
    }

    private Cursor createAttendeeCursor(String id, String expense, String user) {
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getString(0)).thenReturn(id);
        when(c.getColumnIndex(EXPENSE)).thenReturn(1);
        when(c.getString(1)).thenReturn(expense);
        when(c.getColumnIndex(PARTICIPANT)).thenReturn(2);
        when(c.getString(2)).thenReturn(user);

        return c;
    }
}
