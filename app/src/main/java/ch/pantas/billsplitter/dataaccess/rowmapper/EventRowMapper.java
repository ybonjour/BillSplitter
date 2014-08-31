package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;

import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase.EventTable.NAME;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(NAME);

        int id = cursor.getInt(idIdx);
        String name = cursor.getString(nameIdx);

        return new Event(id, name);
    }
}
