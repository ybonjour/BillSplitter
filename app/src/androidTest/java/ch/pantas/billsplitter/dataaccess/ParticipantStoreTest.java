package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static org.mockito.Mockito.when;

public class ParticipantStoreTest extends BaseStoreTest {

    @Inject
    private ParticipantStore store;

    @Mock
    private ParticipantRowMapper mapper;

    @Mock
    private Participant participant;

    @Mock
    private UserStore userStore;

    @SmallTest
    public void testGetParticipantsThrowsNullPointerExceptionIfNoExpenseIdProvided(){
        try {
            store.getParticipants(null);
            fail("No exception has been thrown");
        } catch(NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsThrowsIllegalArgumentExceptionIfEmptyExpenseIdProvided(){
        try {
            store.getParticipants("");
            fail("No exception has been thrown");
        } catch(IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsReturnsEmptyUserListIfNoExpensesExist(){
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<User> users = store.getParticipants("abc");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetParticipantsReturnsCorrectUser(){
        // Given
        User user = new User("userId", "Joe");
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(participant);
        when(participant.getUser()).thenReturn(user.getId());
        when(userStore.getById(user.getId())).thenReturn(user);

        // When
        List<User> users = store.getParticipants("abc");

        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @SmallTest
    public void testGetParticipantsDoesNotReturnUserIfItDoesNotExist(){
        // Given
        String userId = "userId";
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(participant);
        when(participant.getUser()).thenReturn(userId);
        when(userStore.getById(userId)).thenReturn(null);

        // When
        List<User> users = store.getParticipants("abc");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }
}
