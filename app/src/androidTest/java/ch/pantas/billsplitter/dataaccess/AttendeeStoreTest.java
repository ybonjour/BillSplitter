package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.AttendeeRowMapper;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.TABLE;
import static org.mockito.Mockito.when;

public class AttendeeStoreTest extends BaseStoreTest {

    @Inject
    private AttendeeStore store;

    @Mock
    private AttendeeRowMapper mapper;

    @Mock
    private Attendee attendee;

    @Mock
    private ParticipantStore participantStore;

    @SmallTest
    public void testGetAttendeesThrowsNullPointerExceptionIfNoExpenseIdProvided() {
        try {
            store.getAttendees(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetAttendeesThrowsIllegalArgumentExceptionIfEmptyExpenseIdProvided() {
        try {
            store.getAttendees("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetAttendeesReturnsEmptyParticipantListIfNoExpensesExist() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<Participant> participants = store.getAttendees("abc");

        // Then
        assertNotNull(participants);
        assertEquals(0, participants.size());
    }

    @SmallTest
    public void testGetAttendeesReturnsCorrectParticipant() {
        // Given
        Participant participant = new Participant("participantId", "userId", "eventId");
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(attendee);
        when(attendee.getParticipant()).thenReturn(participant.getId());
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        List<Participant> participants = store.getAttendees(participant.getId());

        // Then
        assertNotNull(participants);
        assertEquals(1, participants.size());
        assertEquals(participant, participants.get(0));
    }

    @SmallTest
    public void testGetAttendeesDoesNotReturnUserIfItDoesNotExist() {
        // Given
        String participantId = "participantId";
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(attendee);
        when(attendee.getParticipant()).thenReturn(participantId);
        when(participantStore.getById(participantId)).thenReturn(null);
        when(mapper.getTableName()).thenReturn(TABLE);

        // When
        List<Participant> participants = store.getAttendees("abc");

        // Then
        assertNotNull(participants);
        assertEquals(0, participants.size());
    }
}
