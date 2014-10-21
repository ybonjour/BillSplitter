package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.ui.fragment.DebtsFragment;
import ch.pantas.billsplitter.ui.fragment.ExpensesFragment;
import ch.pantas.billsplitter.ui.fragment.ParticipantsFragment;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static roboguice.RoboGuice.getInjector;

public class EventDetailTabs {

    @Inject
    private Context context;

    private Event event;

    private static final String[] labels = new String[]{
            "Overview",
            "Expenses",
            "Participants"
    };

    public EventDetailTabs init(Event event) {
        checkNotNull(event);
        this.event = event;

        return this;
    }



    public String getLabel(int position) {
        return labels[position];
    }

    public Fragment getFragment(int position) {
        checkNotNull(event);
        switch (position) {
            case 0:
                return getInjector(context).getInstance(DebtsFragment.class);
            case 1:
                return getInjector(context).getInstance(ExpensesFragment.class);
            case 2:
                return getInjector(context).getInstance(ParticipantsFragment.class);
            default:
                throw new IllegalArgumentException("position");
        }
    }

    public int numTabs() {
        return 3;
    }
}
