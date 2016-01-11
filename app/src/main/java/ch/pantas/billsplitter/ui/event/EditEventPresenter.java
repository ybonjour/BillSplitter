package ch.pantas.billsplitter.ui.event;

import android.os.Bundle;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;

public class EditEventPresenter {

    @Inject
    private EventStore eventStore;

    private EditEventView view;

    private Event event;

    public void setView(EditEventView view) {
        this.view = view;
    }

    public void onSetup(Bundle arguments) {
        UUID eventId = (UUID) arguments.getSerializable(ARGUMENT_EVENT_ID);
        event = eventStore.getById(eventId);

        String editTemplate = view.getString(R.string.edit_template);
        view.setupTitle(String.format(editTemplate, event.getName()));

        view.setEventName(event.getName());

        view.setUpCurrencyAdapter(event.getCurrency());
    }

    public void onSaveEvent() {
        String eventName = validateEventName();
        if (eventName == null) return;

        event.setName(eventName);
        event.setCurrency(view.getSelectedCurrency());

        eventStore.persist(event);
        view.openEventDetails(event);
    }

    private String validateEventName() {
        String eventName = view.getEventName();
        if (eventName.isEmpty()) {
            view.setEventNameError();
            return null;
        } else {
            return eventName;
        }
    }
}
