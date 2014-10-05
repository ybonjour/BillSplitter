package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class AddEvent extends RoboActivity {

    @InjectView(R.id.event_name)
    private EditText eventNameField;

    @Inject
    private EventStore eventStore;

    @Inject
    private ActivityStarter activityStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        setTitle(R.string.add_event);
    }

    public void onNext(View v) {
        String eventName = eventNameField.getText().toString();
        if (eventName.isEmpty()) {
            eventNameField.setBackgroundColor(getResources().getColor(R.color.error_color));
        } else {
            Event event = new Event(eventName);
            eventStore.persist(event);
            activityStarter.startAddParticipants(this, event);
            finish();
        }
    }
}
