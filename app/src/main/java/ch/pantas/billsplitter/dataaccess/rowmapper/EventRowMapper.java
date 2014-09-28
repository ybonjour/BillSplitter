package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.NAME;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.TABLE;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(NAME);

        String id = cursor.getString(idIdx);
        String name = cursor.getString(nameIdx);

        return new Event(id, name);
    }

    @Override
    public ContentValues values(Event event) {
        checkNotNull(event);

        ContentValues values = new ContentValues();

        if (event.getId() != null) values.put(ID, event.getId());
        values.put(NAME, event.getName());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
