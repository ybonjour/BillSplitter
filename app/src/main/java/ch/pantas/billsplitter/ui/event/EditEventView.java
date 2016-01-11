package ch.pantas.billsplitter.ui.event;

import com.google.inject.ImplementedBy;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.SupportedCurrency;

@ImplementedBy(EditEventDialog.class)
public interface EditEventView {

    void setupTitle(String title);

    void openEventDetails(Event event);

    String getEventName();

    void setEventNameError();

    SupportedCurrency getSelectedCurrency();

    void setUpCurrencyAdapter(SupportedCurrency currency);

    String getString(int resId);

    void setEventName(String name);
}
