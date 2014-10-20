package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.AMOUNT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.DESCRIPTION;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.USER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpenseRowMapperTest extends BaseMockitoInstrumentationTest {

    @Inject
    private ExpenseRowMapper mapper;

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
        String eventId = UUID.randomUUID().toString();
        String payerId = UUID.randomUUID().toString();
        String description = "test";
        int amount = 10;
        Cursor c = createExpenseCursor(id, eventId, payerId, description, amount);

        // When
        Expense expense = mapper.map(c);

        // Then
        assertNotNull(expense);
        assertEquals(id, expense.getId());
        assertEquals(eventId, expense.getEventId());
        assertEquals(payerId, expense.getPayerId());
        assertEquals(description, expense.getDescription());
        assertEquals(amount, expense.getAmount());
    }

    private Cursor createExpenseCursor(String id, String eventId, String payerId, String description, int amount){
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getString(0)).thenReturn(id);
        when(c.getColumnIndex(EVENT)).thenReturn(1);
        when(c.getString(1)).thenReturn(eventId);
        when(c.getColumnIndex(USER)).thenReturn(2);
        when(c.getString(2)).thenReturn(payerId);
        when(c.getColumnIndex(DESCRIPTION)).thenReturn(3);
        when(c.getString(3)).thenReturn(description);
        when(c.getColumnIndex(AMOUNT)).thenReturn(4);
        when(c.getInt(4)).thenReturn(amount);
        return c;
    }
}