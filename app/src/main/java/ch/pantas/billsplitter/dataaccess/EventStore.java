package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.EventTable.TABLE;

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

}
