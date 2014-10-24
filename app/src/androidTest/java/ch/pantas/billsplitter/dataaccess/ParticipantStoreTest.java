package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.framework.CustomMatchers;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParticipantStoreTest extends BaseStoreTest {

    @Inject
    private ParticipantStore store;

    @Mock
    private ParticipantRowMapper mapper;

    @Mock
    private UserStore userStore;

    @SmallTest
    public void testGetParticipantsThrowsNullPointerExceptionIfNoEventIdProvided(){
        try{
            store.getParticipants(null);
            fail("No exception has been thrown");
        } catch(NullPointerException e){
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsThrowsIllegalArgumentExceptionIfEmptyEventIdProvided(){
        try{
            store.getParticipants("");
            fail("No exception has been thrown");
        } catch(IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsReturnsEmptyListIfNoParticipantsForThatEventExist(){
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<Participant> users = store.getParticipants("someEventId");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetParticipantsReturnsCorrectUser(){
        // Given
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Joe");
        when(userStore.getById(userId)).thenReturn(user);

        String id = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();
        Participant participant = new Participant(id, userId, eventId);
        when(mapper.map(cursor)).thenReturn(participant);

        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);

        // When
        List<Participant> users = store.getParticipants(eventId);

        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @SmallTest
    public void testGetParticipantsDoesNotReturnUserIfItDoesNotExist(){
        // Given
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();
        Participant participant = new Participant(id, userId, eventId);
        when(mapper.map(cursor)).thenReturn(participant);
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);

        when(userStore.getById(userId)).thenReturn(null);

        // When
        List<Participant> users = store.getParticipants(eventId);

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetParticipantsCallsQueryWithCorrectWhereClause(){
        // Given
        String eventId = UUID.randomUUID().toString();
        when(cursor.moveToNext()).thenReturn(false);

        // When
        store.getParticipants(eventId);

        // Then
        verify(database, times(1)).query(anyString(), argThat(allOf(hasSize(1), hasEntry(EVENT, eventId))));

    }
}
