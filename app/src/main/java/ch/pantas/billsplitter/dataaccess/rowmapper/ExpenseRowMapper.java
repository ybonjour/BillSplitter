package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;

import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.AMOUNT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.DESCRIPTION;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.ID;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.USER;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpenseRowMapper implements RowMapper<Expense> {
    @Override
    public Expense map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int eventIdx = cursor.getColumnIndex(EVENT);
        int userIdx = cursor.getColumnIndex(USER);
        int descriptionIdx = cursor.getColumnIndex(DESCRIPTION);
        int amountIdx = cursor.getColumnIndex(AMOUNT);

        String id = cursor.getString(idIdx);
        String eventId = cursor.getString(eventIdx);
        String userId = cursor.getString(userIdx);
        String description = cursor.getString(descriptionIdx);
        double amount = cursor.getDouble(amountIdx);

        return new Expense(id, eventId, userId, description, amount);
    }
}