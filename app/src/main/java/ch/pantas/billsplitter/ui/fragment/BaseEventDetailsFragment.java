package ch.pantas.billsplitter.ui.fragment;

import ch.pantas.billsplitter.model.Event;
import roboguice.fragment.RoboFragment;

public class BaseEventDetailsFragment extends RoboFragment {

    private Event currentEvent;

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
}
