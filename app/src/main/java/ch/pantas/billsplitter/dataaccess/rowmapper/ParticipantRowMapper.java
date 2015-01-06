package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.CONFIRMED;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.LAST_UPDATED;
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
        int confirmedIdx = cursor.getColumnIndex(CONFIRMED);
        int lastUpdatedIdx = cursor.getColumnIndex(LAST_UPDATED);

        UUID id = UUID.fromString(cursor.getString(idIdx));
        UUID user =  UUID.fromString(cursor.getString(userIdx));
        UUID event =  UUID.fromString(cursor.getString(eventIdx));
        boolean confirmed = cursor.getInt(confirmedIdx) == 1;
        long lastUpdated = cursor.getLong(lastUpdatedIdx);

        return new Participant(id, user, event, confirmed, lastUpdated);
    }

    @Override
    public ContentValues getValues(Participant participant) {
        checkNotNull(participant);

        ContentValues values = new ContentValues();
        values.put(ID, participant.getId().toString());
        values.put(USER, participant.getUserId().toString());
        values.put(EVENT, participant.getEventId().toString());
        values.put(CONFIRMED, participant.isConfirmed() ? 1 : 0);
        values.put(LAST_UPDATED, participant.getLastUpdated());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
