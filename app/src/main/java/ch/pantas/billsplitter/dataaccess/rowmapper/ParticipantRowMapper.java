package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;


public class ParticipantRowMapper implements RowMapper<Participant> {
    @Override
    public Participant map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int userIdx = cursor.getColumnIndex(USER);
        int eventIdx = cursor.getColumnIndex(EVENT);

        String id = cursor.getString(idIdx);
        String user = cursor.getString(userIdx);
        String event = cursor.getString(eventIdx);

        return new Participant(id, user, event);
    }

    @Override
    public ContentValues getValues(Participant model) {
        checkNotNull(model);

        ContentValues values = new ContentValues();
        values.put(ID, model.getId());
        values.put(USER, model.getUserId());
        values.put(EVENT, model.getEventId());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
