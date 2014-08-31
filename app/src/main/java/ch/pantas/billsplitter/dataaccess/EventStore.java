package ch.pantas.billsplitter.dataaccess;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.model.Event;

@Singleton
public class EventStore {

    @Inject
    private BillSplitterDatabase dbHelper;

    @Inject
    private EventRowMapper mapper;

    public List<Event> getAllEvents() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + BillSplitterDatabase.EventTable.TABLE, null);

        ArrayList<Event> events = new ArrayList<Event>();
        while (cursor.moveToNext()) {
            events.add(mapper.map(cursor));
        }

        return events;
    }

}
