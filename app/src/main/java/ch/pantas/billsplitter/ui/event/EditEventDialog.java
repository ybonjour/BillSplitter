package ch.pantas.billsplitter.ui.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.splitty.R;
import roboguice.fragment.RoboDialogFragment;

public class EditEventDialog extends RoboDialogFragment implements EditEventView {

    @Inject
    private EditEventPresenter presenter;

    @Inject
    private ActivityStarter activityStarter;

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presenter.setView(this);

        root = inflater.inflate(R.layout.edit_event_dialog, container);

        presenter.onSetup(getArguments());

        Button editEventButton = (Button) root.findViewById(R.id.edit_event_button);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSaveEvent();
            }
        });

        return root;
    }

    @Override
    public void setupTitle(String title) {
        getDialog().setTitle(title);
    }

    @Override
    public void openEventDetails(Event event) {
        dismiss();
        activityStarter.startEventDetails(getActivity(), event, true);
    }

    @Override
    public String getEventName() {
        return getEventNameEditText().getText().toString();
    }

    @Override
    public void setEventNameError() {
        getEventNameEditText().setBackgroundColor(getResources().getColor(R.color.error_color));
    }

    @Override
    public SupportedCurrency getSelectedCurrency() {
        return SupportedCurrency.valueOf(getCurrencySpinner().getSelectedItem().toString());
    }

    @Override
    public void setUpCurrencyAdapter(SupportedCurrency currency) {
        Spinner spinnerView = getCurrencySpinner();

        ArrayAdapter<CharSequence> currencyAdapter = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_item, SupportedCurrency.getValuesAsString());

        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(currencyAdapter);

        spinnerView.setSelection(currencyAdapter.getPosition(currency.toString()));
    }

    @Override
    public void setEventName(String name) {
        getEventNameEditText().setText(name);
    }

    private Spinner getCurrencySpinner() {
        return (Spinner) root.findViewById(R.id.event_currency);
    }

    private EditText getEventNameEditText() {
        return (EditText) root.findViewById(R.id.event_name);
    }
}
