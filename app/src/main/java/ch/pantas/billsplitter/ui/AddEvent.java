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

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class AddEvent extends RoboActivity {

    @InjectView(R.id.event_name)
    private EditText eventNameField;

    @Inject
    private EventStore eventStore;

    @Inject
    private ActivityStarter activityStarter;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        if (eventId == null) {
            setTitle(R.string.add_event);
        }
        else {
            event = eventStore.getById(eventId);
            checkNotNull(event);
            setTitle("Edit " + event.getName());
            eventNameField.setText(event.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
    }

    public void onNext(View v) {
        String eventName = eventNameField.getText().toString();
        if (eventName.isEmpty()) {
            eventNameField.setBackgroundColor(getResources().getColor(R.color.error_color));
        } else {
            if (event == null) {
                event = new Event(eventName);
            }
            else {
                event.setName(eventName);
            }
            eventStore.persist(event);
            activityStarter.startAddParticipants(this, event);
            finish();
        }
    }
}
