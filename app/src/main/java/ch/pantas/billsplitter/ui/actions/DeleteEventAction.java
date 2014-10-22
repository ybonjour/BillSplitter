package ch.pantas.billsplitter.ui.actions;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.ui.EventDetails;
import ch.yvu.myapplication.R;

public class DeleteEventAction implements EventDetailsAction {

    @Inject
    private EventService eventService;

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(final EventDetails activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.delete_event_alert_message))
                .setCancelable(false)
                .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setPositiveButton(activity.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEvent(activity);
                    }
                })
                .setTitle(activity.getResources().getString(R.string.delete_event_alert_title))
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();

        return true;
    }

    public void deleteEvent(EventDetails activity) {
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
    }
}
