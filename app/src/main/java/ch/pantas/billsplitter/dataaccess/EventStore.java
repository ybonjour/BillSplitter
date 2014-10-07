package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.ID;
@Singleton
public class EventStore extends BaseStore<Event> {

    @Inject
    public EventStore(EventRowMapper mapper) {
        super(mapper);
    }

    public void removeAll(String eventId) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(ID, eventId);
        removeAll(where);
    }
}
