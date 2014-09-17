package ch.pantas.billsplitter.dataaccess;

import android.content.ContentValues;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.NAME;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.TABLE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class EventStore extends BaseStore<Event> {

    @Inject
    public EventStore(EventRowMapper mapper) {
        super(mapper);
    }

    public List<Event> getAllEvents() {
        String sql = "SELECT * FROM " + TABLE;
        return getModelsByQuery(sql, null);
    }

    public String insertEvent(String name) {
        checkNotNull(name);
        checkArgument(!name.isEmpty());
        ContentValues values = new ContentValues();
        String eventId = UUID.randomUUID().toString();
        values.put(ID, eventId);
        values.put(NAME, name);
        insertRow(TABLE, values);
        return eventId;
    }

}
