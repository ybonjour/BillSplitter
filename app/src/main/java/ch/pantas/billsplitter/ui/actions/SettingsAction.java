package ch.pantas.billsplitter.ui.actions;

import com.google.inject.Inject;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.ui.EventDetails;

public class SettingsAction implements EventDetailsAction{

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        activityStarter.startSettings(activity);
        return true;
    }
}
