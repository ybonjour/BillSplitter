package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class EventList extends RoboActivity {

    @Inject
    private EventStore store;

    @Inject
    private ActivityStarter activityStarter;

    @InjectView(R.id.event_list)
    private ListView eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_list);
        setTitle(R.string.events_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Event event = (Event) eventList.getAdapter().getItem(position);
                activityStarter.startEventDetails(EventList.this, event);
            }
        });
        reloadList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventList.setOnItemClickListener(null);
    }

    private void reloadList() {
        List<Event> events = store.getAll();
        ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1, events);
        eventList.setAdapter(adapter);
    }

    public void onAddEvent(View view) {
        activityStarter.startAddEvent(this);
    }
}
