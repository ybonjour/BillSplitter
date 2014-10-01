package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParticipantRowMapperTest extends BaseMockitoInstrumentationTest {

    @Inject
    private ParticipantRowMapper mapper;

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
        String user = UUID.randomUUID().toString();
        Cursor c = createParticipantCursor(id, expense, user);

        // When
        Participant participant = mapper.map(c);

        // Then
        assertNotNull(participant);
        assertEquals(id, participant.getId());
        assertEquals(expense, participant.getExpense());
        assertEquals(user, participant.getUser());
    }

    @SmallTest
    public void testGetValuesThrowsExceptionIfNoParticipantProvided() {
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
        String id = UUID.randomUUID().toString();
        String expense = UUID.randomUUID().toString();
        String user = UUID.randomUUID().toString();
        Participant participant = new Participant(id, expense, user);

        // When
        ContentValues values = mapper.getValues(participant);

        // Then
        assertNotNull(values);
        assertEquals(3, values.size());
        assertEquals(id, values.get(ID));
        assertEquals(expense, values.get(EXPENSE));
        assertEquals(user, values.get(USER));
    }

    private Cursor createParticipantCursor(String id, String expense, String user) {
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getString(0)).thenReturn(id);
        when(c.getColumnIndex(EXPENSE)).thenReturn(1);
        when(c.getString(1)).thenReturn(expense);
        when(c.getColumnIndex(USER)).thenReturn(2);
        when(c.getString(2)).thenReturn(user);

        return c;
    }
}
