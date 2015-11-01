package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import ch.pantas.splitty.R;
import roboguice.fragment.RoboDialogFragment;

import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;

public class NewEventDialog extends RoboDialogFragment {
    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private EventStore eventStore;

    @Inject
    private EventService eventService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.new_event_dialog, container);


        ArrayAdapter<CharSequence> currencyAdapter = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_item, SupportedCurrency.getValuesAsString());
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getCurrencySpinner(root).setAdapter(currencyAdapter);

        UUID eventId = getArguments() != null ? (UUID) getArguments().getSerializable(ARGUMENT_EVENT_ID) : null;
        if (eventId == null) {
            setUpCreateDialog(root, currencyAdapter);
        } else {
            setUpEditDialog(root, currencyAdapter, eventId);
        }

        return root;
    }

    private void setUpCreateDialog(final View root, ArrayAdapter<CharSequence> currencyAdapter) {
        getDialog().setTitle(R.string.add_event);
        getCurrencySpinner(root).setSelection(currencyAdapter.getPosition(getUserCurrency().toString()));
        LinearLayout create_buttons = (LinearLayout) root.findViewById(R.id.create_buttons);
        create_buttons.setVisibility(View.VISIBLE);

        Button createEventButton = (Button) root.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventName = validateEventName(root);
                if (eventName == null) return;

                Event event = eventService.createEvent(getEventName(root).getText().toString(), getSelectedCurrency(root));
                dismiss();
                activityStarter.startEventDetails(getActivity(), event.getId(), true, 2);
            }
        });

        Button joinEventButton = (Button) root.findViewById(R.id.join_event_button);
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityStarter.startJoinEvent(getActivity());
                dismiss();
            }
        });

    }

    private void setUpEditDialog(final View root, ArrayAdapter<CharSequence> currencyAdapter, UUID eventId) {
        final Event event = eventStore.getById(eventId);
        String editTemplate = getString(R.string.edit_template);
        getDialog().setTitle(String.format(editTemplate, event.getName()));
        EditText eventName = getEventName(root);
        eventName.setText(event.getName());
        getCurrencySpinner(root).setSelection(currencyAdapter.getPosition(event.getCurrency().toString()));
        LinearLayout edit_buttons = (LinearLayout) root.findViewById(R.id.edit_buttons);
        edit_buttons.setVisibility(View.VISIBLE);

        Button editEventButton = (Button) root.findViewById(R.id.edit_event_button);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventName = validateEventName(root);
                if (eventName == null) return;

                event.setName(eventName);
                event.setCurrency(getSelectedCurrency(root));

                eventStore.persist(event);
                dismiss();

                activityStarter.startEventDetails(getActivity(), event, true);
            }
        });
    }

    private String validateEventName(View root) {
        String eventName = getEventName(root).getText().toString();
        if (eventName.isEmpty()) {
            getEventName(root).setBackgroundColor(getResources().getColor(R.color.error_color));
            return null;
        } else {
            return eventName;
        }
    }

    private SupportedCurrency getSelectedCurrency(View root) {
        return SupportedCurrency.valueOf(getCurrencySpinner(root).getSelectedItem().toString());
    }

    private EditText getEventName(View root) {
        return (EditText) root.findViewById(R.id.event_name);
    }

    private Spinner getCurrencySpinner(View root) {
        return (Spinner) root.findViewById(R.id.event_currency);
    }

    private SupportedCurrency getUserCurrency() {
        Currency userCurrency = Currency.getInstance(Locale.getDefault());

        try {
            return SupportedCurrency.valueOf(userCurrency.getCurrencyCode());
        } catch (IllegalArgumentException e) {
            return SupportedCurrency.EUR;
        }
    }
}