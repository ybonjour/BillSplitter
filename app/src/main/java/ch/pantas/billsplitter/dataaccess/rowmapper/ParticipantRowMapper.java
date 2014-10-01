package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ParticipantRowMapper implements RowMapper<Participant> {
    @Override
    public Participant map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int expenseIdx = cursor.getColumnIndex(EXPENSE);
        int userIdx = cursor.getColumnIndex(USER);

        String id = cursor.getString(idIdx);
        String expense = cursor.getString(expenseIdx);
        String user = cursor.getString(userIdx);

        return new Participant(id, expense, user);
    }

    @Override
    public ContentValues getValues(Participant participant) {
        ContentValues values = new ContentValues();
        values.put(ID, participant.getId());
        values.put(EXPENSE, participant.getExpense());
        values.put(USER, participant.getUser());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
