package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.AttendeeRowMapper;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.USER;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AttendeeStoreTest extends BaseStoreTest {

    @Inject
    private AttendeeStore store;

    @Mock
    private AttendeeRowMapper mapper;

    @Mock
    private Attendee attendee;

    @Mock
    private UserStore userStore;

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
    public void testGetAttendeesReturnsEmptyUserListIfNoExpensesExist() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<User> users = store.getAttendees("abc");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetAttendeesReturnsCorrectUser() {
        // Given
        User user = new User("userId", "Joe");
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(attendee);
        when(attendee.getUser()).thenReturn(user.getId());
        when(userStore.getById(user.getId())).thenReturn(user);

        // When
        List<User> users = store.getAttendees("abc");

        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @SmallTest
    public void testGetAttendeesDoesNotReturnUserIfItDoesNotExist() {
        // Given
        String userId = "userId";
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(attendee);
        when(attendee.getUser()).thenReturn(userId);
        when(userStore.getById(userId)).thenReturn(null);
        when(mapper.getTableName()).thenReturn(TABLE);

        // When
        List<User> users = store.getAttendees("abc");

        // Then
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @SmallTest
    public void testGetAttendeeByExpenseAndUserReturnsNullIfNoAttendeeExists() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        Attendee result = store.getAttendeeByExpenseAndUser("expenseId", "userId");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetAttendeeByExpenseAndUserReturnsCorrectAttendee() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(attendee);

        // When
        Attendee result = store.getAttendeeByExpenseAndUser("expenseId", "userId");

        // Then
        assertEquals(attendee, result);
    }

    @SmallTest
    public void testGetAttendeeByExpenseAndUserHasCorrectWhereArgument() {
        // Given
        String expense = "expenseId";
        String user = "userId";

        // When
        store.getAttendeeByExpenseAndUser(expense, user);

        // Then
        verify(database, times(1)).query(anyString(), argThat(allOf(
                hasSize(2),
                hasEntry(EXPENSE, expense),
                hasEntry(USER, user))));
    }
}
