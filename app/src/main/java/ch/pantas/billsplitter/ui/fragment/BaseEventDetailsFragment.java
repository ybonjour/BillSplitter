package ch.pantas.billsplitter.ui.fragment;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.ui.adapter.EventDetailPagerAdapter;
import roboguice.fragment.RoboFragment;

public class BaseEventDetailsFragment extends RoboFragment {

    private Event currentEvent;
    private EventDetailPagerAdapter pagerAdapter;

    public BaseEventDetailsFragment init(Event event){
        this.currentEvent = event;
        return this;
    }

    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    protected Event getCurrentEvent() {
        return currentEvent;
    }

    public void setPagerAdapter(EventDetailPagerAdapter adapter) {
        this.pagerAdapter = adapter;
    }

    protected EventDetailPagerAdapter getPagerAdapter(){
        return this.pagerAdapter;
    }
}
