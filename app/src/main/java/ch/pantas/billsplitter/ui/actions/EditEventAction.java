package ch.pantas.billsplitter.ui.actions;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.ui.EventDetails;

public class EditEventAction implements EventDetailsAction {
    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();
        if(event != null) {
            activityStarter.startEditEvent(activity, event);
        }
        return true;
    }
}
