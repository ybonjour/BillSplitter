package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Currency;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class AddEvent extends RoboActivity {

    @InjectView(R.id.event_name)
    private EditText eventNameField;
    @InjectView(R.id.event_currency)
    private Spinner currencySpinner;

    @Inject
    private EventStore eventStore;

    @Inject
    private ActivityStarter activityStarter;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        ArrayAdapter<CharSequence> currencyAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, Currency.getValuesAsString());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        if (eventId == null) {
            setTitle(R.string.add_event);
        }
        else {
            event = eventStore.getById(eventId);
            checkNotNull(event);
            setTitle("Edit " + event.getName());
            eventNameField.setText(event.getName());
            currencySpinner.setSelection(currencyAdapter.getPosition(event.getCurrency().toString()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
    }

    public void onNext() {
        String eventName = eventNameField.getText().toString();
        if (eventName.isEmpty()) {
            eventNameField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        Currency currency = Currency.valueOf(currencySpinner.getSelectedItem().toString());
        if (event == null) {
            event = new Event(eventName, currency);
        }
        else {
            event.setName(eventName);
            event.setCurrency(currency);
        }

        if(event.isNew()){
            eventStore.persist(event);
            activityStarter.startAddParticipants(this, event);
        } else {
            eventStore.persist(event);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_add_event_next == item.getItemId()) {
            onNext();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
