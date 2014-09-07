package ch.pantas.billsplitter.dataaccess;

import android.database.Cursor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

@Singleton
public class EventStore {

    @Inject
    private BillSplitterDatabaseOpenHelper dbHelper;

    @Inject
    private EventRowMapper mapper;

    public List<Event> getAllEvents() {
        BillSplitterDatabase db = dbHelper.getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + BillSplitterDatabaseOpenHelper.EventTable.TABLE, null);

        ArrayList<Event> events = new ArrayList<Event>();
        while (cursor.moveToNext()) {
            events.add(mapper.map(cursor));
        }

        return events;
    }

}
