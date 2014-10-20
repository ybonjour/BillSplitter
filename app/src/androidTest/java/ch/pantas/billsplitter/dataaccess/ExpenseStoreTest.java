package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.ExpenseRowMapper;
import ch.pantas.billsplitter.model.Expense;

import static org.mockito.Mockito.when;

public class ExpenseStoreTest extends BaseStoreTest {
    @Inject
    private ExpenseStore expenseStore;

    @Mock
    private ExpenseRowMapper mapper;

    @SmallTest
    public void testNoExpensesOfEvent() {
        // Given
        String eventId = UUID.randomUUID().toString();
        expenseStore.getExpensesOfEvent(eventId);
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);

        // Then
        assertEquals(0, expenses.size());
    }

    @SmallTest
     public void testExpensesOfEvent() {
        // Given
        Expense e = new Expense("a", "b", "c", "d", 10);
        String eventId = UUID.randomUUID().toString();
        expenseStore.getExpensesOfEvent(eventId);
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(e).thenReturn(null);

        // When
        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);

        // Then
        assertEquals(1, expenses.size());
        assertEquals(e, expenses.get(0));
    }

    @SmallTest
    public void testExpensesOfUser() {
        // Given
        Expense e = new Expense("a", "b", "c", "d", 10);
        String userId = UUID.randomUUID().toString();
        expenseStore.getExpensesOfUser(userId);
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(e).thenReturn(null);

        // When
        List<Expense> expenses = expenseStore.getExpensesOfEvent(userId);

        // Then
        assertEquals(1, expenses.size());
        assertEquals(e, expenses.get(0));
    }
}
