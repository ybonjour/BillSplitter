package ch.pantas.billsplitter.ui.actions;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.ui.EventDetails;


public class DeleteEventAction implements EventDetailsAction {

    @Inject
    private EventService eventService;

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();
        if(event != null){
            Event newActiveEvent = eventService.removeEventAndGetActiveEvent(event);
            if(newActiveEvent == null) {
                activityStarter.startStartEvent(activity);
                activity.finish();
            } else {
                activityStarter.startEventDetails(activity, newActiveEvent, false);
            }
        }
        return true;
    }
}
