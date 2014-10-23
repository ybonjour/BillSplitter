package ch.pantas.billsplitter.ui.actions;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.ui.EventDetails;
import ch.pantas.splitty.R;

public class AddExpenseAction implements EventDetailsAction {

    @Inject
    private ActivityStarter activityStarter;

    @Override
    public boolean execute(EventDetails activity) {
        Event event = activity.getEvent();
        if(event != null) {
            // Ensure we go to the Expenses tab when we return
            activity.setCurrentTab(activity.getTabPosition(activity.getString(R.string.expenses)));
            activityStarter.startAddExpense(activity, event);
        }
        return true;
    }
}
