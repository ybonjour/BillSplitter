package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.ui.fragment.DebtsFragment;
import ch.pantas.billsplitter.ui.fragment.ExpensesFragment;
import ch.pantas.billsplitter.ui.fragment.ParticipantsFragment;
import ch.pantas.splitty.R;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static roboguice.RoboGuice.getInjector;

public class EventDetailTabs {

    @Inject
    private Context context;

    private Event event;

    private List<String> labels;

    public EventDetailTabs init(Event event) {
        checkNotNull(event);
        this.event = event;

        labels = asList(
                context.getString(R.string.overview),
                context.getString(R.string.expenses),
                context.getString(R.string.participants));

        return this;
    }

    public int getTabPosition(String label) {
        return labels.indexOf(label);
    }

    public Event getEvent() {
        return event;
    }

    public String getLabel(int position) {
        return labels.get(position);
    }

    public Fragment getFragment(int position) {
        checkNotNull(event);
        switch (position) {
            case 0:
                return getInjector(context).getInstance(DebtsFragment.class).init(event);
            case 1:
                return getInjector(context).getInstance(ExpensesFragment.class).init(event);
            case 2:
                return getInjector(context).getInstance(ParticipantsFragment.class).init(event);
            default:
                throw new IllegalArgumentException("position");
        }
    }

    public int numTabs() {
        return 3;
    }
}
