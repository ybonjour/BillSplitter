package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.CURRENCY;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.NAME;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.OWNER;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(NAME);
        int currencyIdx = cursor.getColumnIndex(CURRENCY);
        int ownerIdx = cursor.getColumnIndex(OWNER);

        UUID id = UUID.fromString(cursor.getString(idIdx));
        String name = cursor.getString(nameIdx);
        SupportedCurrency currency = SupportedCurrency.valueOf(cursor.getString(currencyIdx));
        UUID ownerId = UUID.fromString(cursor.getString(ownerIdx));

        return new Event(id, name, currency, ownerId);
    }

    @Override
    public ContentValues getValues(Event event) {
        checkNotNull(event);

        ContentValues values = new ContentValues();

        if (event.getId() != null) values.put(ID, event.getId().toString());
        values.put(NAME, event.getName());
        values.put(CURRENCY, event.getCurrency().toString());
        values.put(OWNER, event.getOwnerId().toString());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
