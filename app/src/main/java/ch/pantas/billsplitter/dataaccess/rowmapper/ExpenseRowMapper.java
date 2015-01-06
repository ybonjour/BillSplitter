package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.AMOUNT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.DESCRIPTION;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.PARTICIPANT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.OWNER;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpenseRowMapper implements RowMapper<Expense> {
    @Override
    public Expense map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int eventIdx = cursor.getColumnIndex(EVENT);
        int payerIdx = cursor.getColumnIndex(PARTICIPANT);
        int descriptionIdx = cursor.getColumnIndex(DESCRIPTION);
        int amountIdx = cursor.getColumnIndex(AMOUNT);
        int ownerIdx = cursor.getColumnIndex(OWNER);

        UUID id = UUID.fromString(cursor.getString(idIdx));
        UUID eventId = UUID.fromString(cursor.getString(eventIdx));
        UUID payerId = UUID.fromString(cursor.getString(payerIdx));
        String description = cursor.getString(descriptionIdx);
        int amount = cursor.getInt(amountIdx);
        UUID ownerId = UUID.fromString(cursor.getString(ownerIdx));

        return new Expense(id, eventId, payerId, description, amount, ownerId);
    }

    @Override
    public ContentValues getValues(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(ID, expense.getId().toString());
        values.put(EVENT, expense.getEventId().toString());
        values.put(PARTICIPANT, expense.getPayerId().toString());
        values.put(DESCRIPTION, expense.getDescription());
        values.put(AMOUNT, expense.getAmount());
        values.put(OWNER, expense.getOwnerId().toString());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}