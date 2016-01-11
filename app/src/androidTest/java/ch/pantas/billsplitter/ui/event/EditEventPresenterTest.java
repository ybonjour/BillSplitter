package ch.pantas.billsplitter.ui.event;

import android.os.Bundle;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.ui.EventDetails;
import ch.pantas.splitty.R;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditEventPresenterTest extends BaseMockitoInstrumentationTest {

    @Mock
    private EventStore eventStore;

    @Mock
    private EditEventView view;

    @Inject
    private EditEventPresenter presenter;

    private Bundle arguments;
    private Event event = new Event(UUID.randomUUID(), "Test Event", SupportedCurrency.CHF, UUID.randomUUID());
    private String editTemplate = "Event: %s";


    @Override
    public void setUp() throws Exception {
        super.setUp();
        presenter.setView(view);
        arguments = new Bundle();
        arguments.putSerializable(EventDetails.ARGUMENT_EVENT_ID, event.getId());

        when(eventStore.getById(event.getId())).thenReturn(event);
        when(view.getString(R.string.edit_template)).thenReturn(editTemplate);
    }

    public void testOnSetupSetsCorrectTitle() {
        presenter.onSetup(arguments);

        verify(view).setupTitle(String.format(editTemplate, event.getName()));
    }

    public void testOnSetupSetsCorrectEventName() {
        presenter.onSetup(arguments);

        verify(view).setEventName(event.getName());
    }

    public void testOnSetupSetsCorrectCurrency() {
        presenter.onSetup(arguments);

        verify(view).setUpCurrencyAdapter(event.getCurrency());
    }

    public void testOnSaveEventUpdatesEventNameAndCurrency() {
        presenter.onSetup(arguments);
        String newName = "New Event Name";
        SupportedCurrency newCurrency = SupportedCurrency.USD;
        when(view.getEventName()).thenReturn(newName);
        when(view.getSelectedCurrency()).thenReturn(newCurrency);

        presenter.onSaveEvent();

        assertEquals(newName, event.getName());
        assertEquals(newCurrency, event.getCurrency());
    }

    public void testOnSaveEventPersistsEvent() {
        presenter.onSetup(arguments);
        String newName = "New Event Name";
        SupportedCurrency newCurrency = SupportedCurrency.USD;
        when(view.getEventName()).thenReturn(newName);
        when(view.getSelectedCurrency()).thenReturn(newCurrency);

        presenter.onSaveEvent();

        verify(eventStore).persist(event);
    }

    public void testOnSaveEventOpensEventDetails() {
        presenter.onSetup(arguments);
        String newName = "New Event Name";
        SupportedCurrency newCurrency = SupportedCurrency.USD;
        when(view.getEventName()).thenReturn(newName);
        when(view.getSelectedCurrency()).thenReturn(newCurrency);

        presenter.onSaveEvent();

        verify(view).openEventDetails(event);
    }

    public void testOnSaveShowsErrorIfEventNameIsEmpty() {
        presenter.onSetup(arguments);
        when(view.getEventName()).thenReturn("");

        presenter.onSaveEvent();

        verify(view).setEventNameError();
    }

    public void testOnSaveDoesNotPersistEventIfEventNameIsEmpty() {
        presenter.onSetup(arguments);
        when(view.getEventName()).thenReturn("");

        presenter.onSaveEvent();

        verify(eventStore, never()).persist(any(Event.class));
    }


}
