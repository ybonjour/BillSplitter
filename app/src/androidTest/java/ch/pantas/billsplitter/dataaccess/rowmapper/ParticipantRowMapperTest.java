package ch.pantas.billsplitter.dataaccess.rowmapper;


import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
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
        String user = UUID.randomUUID().toString();
        String event = UUID.randomUUID().toString();
        Cursor c = createParticipantCursor(id, user, event);

        // When
        Participant participant = mapper.map(c);

        // Then
        assertNotNull(participant);
        assertEquals(id, participant.getId());
        assertEquals(user, participant.getUserId());
        assertEquals(event, participant.getEventId());
    }

    @SmallTest
    public void testGetValuesReturnsCorrectValues(){
        // Given
        String id = UUID.randomUUID().toString();
        String user = UUID.randomUUID().toString();
        String event = UUID.randomUUID().toString();
        Participant participant = new Participant(id, user, event);

        // When
        ContentValues values = mapper.getValues(participant);

        // Then
        assertNotNull(values);
        assertEquals(3, values.size());
        assertEquals(id, values.get(ID));
        assertEquals(user, values.get(USER));
        assertEquals(event, values.get(EVENT));
    }

    private Cursor createParticipantCursor(String id, String user, String event) {
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getString(0)).thenReturn(id);

        when(c.getColumnIndex(USER)).thenReturn(1);
        when(c.getString(1)).thenReturn(user);

        when(c.getColumnIndex(EVENT)).thenReturn(2);
        when(c.getString(2)).thenReturn(event);

        return c;
    }


}
