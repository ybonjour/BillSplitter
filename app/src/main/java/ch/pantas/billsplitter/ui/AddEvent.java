package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.inject.Inject;

import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
import static ch.pantas.billsplitter.model.SupportedCurrency.EUR;
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

    @Inject
    private EventService eventService;

    @Inject
    private SharedPreferenceService sharedPreferenceService;


    Event event;

    private SupportedCurrency getUserCurrency() {
        Currency userCurrency = Currency.getInstance(Locale.getDefault());
        try {
            return SupportedCurrency.valueOf(userCurrency.getCurrencyCode());
        } catch (IllegalArgumentException e) {
            return EUR;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        ArrayAdapter<CharSequence> currencyAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, SupportedCurrency.getValuesAsString());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        UUID eventId = (UUID) getIntent().getSerializableExtra(ARGUMENT_EVENT_ID);
        if (eventId == null) {
            setTitle(R.string.add_event);
            currencySpinner.setSelection(currencyAdapter.getPosition(getUserCurrency().toString()));
        } else {
            event = eventStore.getById(eventId);
            checkNotNull(event);
            String editTemplate = getString(R.string.edit_template);
            setTitle(String.format(editTemplate, event.getName()));
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

        SupportedCurrency currency = SupportedCurrency.valueOf(currencySpinner.getSelectedItem().toString());
        if (event == null) {
            event = eventService.createEvent(eventName, currency);
            activityStarter.startAddParticipants(this, event);
        } else {
            event.setName(eventName);
            event.setCurrency(currency);
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
