package ch.pantas.billsplitter.ui.actions;

import android.os.Bundle;
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.EventDetails;
import ch.pantas.billsplitter.ui.NewEventDialog;
import ch.pantas.billsplitter.ui.event.EditEventDialog;
import ch.pantas.splitty.R;
import roboguice.RoboGuice;

import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;

public class EditEventAction implements EventDetailsAction {
    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();

        UUID userId = sharedPreferenceService.getUserId();

        if (userId.equals(event.getOwnerId())) {
            EditEventDialog dialog = new EditEventDialog();

            Bundle arguments = new Bundle();
            arguments.putSerializable(ARGUMENT_EVENT_ID, event.getId());

            dialog.setArguments(arguments);

            dialog.show(activity.getSupportFragmentManager(), "newGroup");
        }
        else {
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.access_denied_event), Toast.LENGTH_LONG);
            toast.show();
        }

        return true;
    }
}
