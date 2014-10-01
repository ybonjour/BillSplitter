package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
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
    private Participant participant;

    @Mock
    private UserStore userStore;

    @SmallTest
    public void testGetParticipantsThrowsNullPointerExceptionIfNoExpenseIdProvided() {
        try {
            store.getParticipants(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsThrowsIllegalArgumentExceptionIfEmptyExpenseIdProvided() {
        try {
            store.getParticipants("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsReturnsEmptyUserListIfNoExpensesExist() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<User> users = store.getParticipants("abc");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetParticipantsReturnsCorrectUser() {
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
    public void testGetParticipantsDoesNotReturnUserIfItDoesNotExist() {
        // Given
        String userId = "userId";
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(participant);
        when(participant.getUser()).thenReturn(userId);
        when(userStore.getById(userId)).thenReturn(null);
        when(mapper.getTableName()).thenReturn(TABLE);

        // When
        List<User> users = store.getParticipants("abc");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetParticipantByExpenseAndUserReturnsNullIfNoParticipantExists() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        Participant result = store.getParticipantByExpenseAndUser("expenseId", "userId");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetParticipantByExpenseAndUserReturnsCorrectParticipant() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(participant);

        // When
        Participant result = store.getParticipantByExpenseAndUser("expenseId", "userId");

        // Then
        assertEquals(participant, result);
    }

    @SmallTest
    public void testGetParticipantByExpenseAndUserHasCorrectWhereArgument() {
        // Given
        String expense = "expenseId";
        String user = "userId";

        // When
        store.getParticipantByExpenseAndUser(expense, user);

        // Then
        verify(database, times(1)).query(anyString(), argThat(allOf(
                hasSize(2),
                hasEntry(EXPENSE, expense),
                hasEntry(USER, user))));
    }
}
