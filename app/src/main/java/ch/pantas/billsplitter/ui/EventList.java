package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboListActivity;

public class EventList extends RoboListActivity {

    @Inject
    private EventStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.events_title);

        updateListAdapter();
    }

    private void updateListAdapter() {
        List<Event> events = store.getAllEvents();
        ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1, events);
        setListAdapter(adapter);
    }
}
