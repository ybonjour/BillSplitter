package ch.pantas.billsplitter.dataaccess;

import android.content.ContentValues;

import com.google.inject.Inject;

import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.rowmapper.ExpenseRowMapper;
import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.AMOUNT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.DESCRIPTION;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.USER;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkArgument;

public class ExpenseStore extends BaseStore<Expense> {

    @Inject
    public ExpenseStore(ExpenseRowMapper mapper) {
        super(mapper);
    }

    public List<Expense> getExpensesOfEvent(String id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + EVENT + " = ?";
        return getModelsByQuery(sql, new String[]{id});
    }

    public List<Expense> getExpensesOfUser(String id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + USER + " = ?";
        return getModelsByQuery(sql, new String[]{id});
    }

    public String insertExpense(String eventId, String userId, String description, double amount) {
        checkNotNull(eventId);
        checkNotNull(userId);
        checkNotNull(description);
        checkArgument(amount>0.0);

        ContentValues values = new ContentValues();
        String expenseId = UUID.randomUUID().toString();
        values.put(ID, expenseId);
        values.put(EVENT, eventId);
        values.put(USER, userId);
        values.put(DESCRIPTION, description);
        values.put(AMOUNT, amount);
        insertRow(TABLE, values);
        return expenseId;
    }
}