package ch.pantas.billsplitter.ui.fragment;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.ui.adapter.EventDetailPagerAdapter;
import roboguice.fragment.RoboFragment;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class BaseEventDetailsFragment extends RoboFragment {

    private static final String EVENT_KEY = "fragment_event";

    private EventDetailPagerAdapter pagerAdapter;

    @Inject
    private EventService eventService;


    public BaseEventDetailsFragment init(EventDetailPagerAdapter pagerAdapter) {
        checkNotNull(pagerAdapter);

        this.pagerAdapter = pagerAdapter;
        return this;
    }

    protected Event getCurrentEvent() {
        return eventService.getActiveEvent();
    }

    public void setPagerAdapter(EventDetailPagerAdapter adapter) {
        checkNotNull(adapter);

        this.pagerAdapter = adapter;
    }

    protected EventDetailPagerAdapter getPagerAdapter() {
        return this.pagerAdapter;
    }
}
