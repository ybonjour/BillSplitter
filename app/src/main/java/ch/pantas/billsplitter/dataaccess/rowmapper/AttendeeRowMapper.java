package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import ch.pantas.billsplitter.model.Attendee;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.PARTICIPANT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class AttendeeRowMapper implements RowMapper<Attendee> {
    @Override
    public Attendee map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int expenseIdx = cursor.getColumnIndex(EXPENSE);
        int participantIdx = cursor.getColumnIndex(PARTICIPANT);

        String id = cursor.getString(idIdx);
        String expense = cursor.getString(expenseIdx);
        String participant = cursor.getString(participantIdx);

        return new Attendee(id, expense, participant);
    }

    @Override
    public ContentValues getValues(Attendee attendee) {
        ContentValues values = new ContentValues();
        values.put(ID, attendee.getId());
        values.put(EXPENSE, attendee.getExpense());
        values.put(PARTICIPANT, attendee.getParticipant());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
