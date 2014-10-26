package ch.pantas.billsplitter.ui.actions;

import android.widget.Toast;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.EventDetails;
import ch.pantas.splitty.R;

public class EditEventAction implements EventDetailsAction {
    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();

        String userId = sharedPreferenceService.getUserId();

        if (userId.equals(event.getOwnerId())) {
            activityStarter.startEditEvent(activity, event);
        }
        else {
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.access_denied_event), Toast.LENGTH_LONG);
            toast.show();
        }

        return true;
    }
}
