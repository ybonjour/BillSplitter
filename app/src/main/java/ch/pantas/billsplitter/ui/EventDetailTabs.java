package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.ui.adapter.EventDetailPagerAdapter;
import ch.pantas.billsplitter.ui.fragment.DebtsFragment;
import ch.pantas.billsplitter.ui.fragment.ExpensesFragment;
import ch.pantas.billsplitter.ui.fragment.ParticipantsFragment;
import ch.pantas.splitty.R;

import static java.util.Arrays.asList;
import static roboguice.RoboGuice.getInjector;

public class EventDetailTabs {

    @Inject
    private Context context;

    private List<String> labels;

    public EventDetailTabs init() {
        labels = asList(
                context.getString(R.string.overview),
                context.getString(R.string.expenses),
                context.getString(R.string.participants));

        return this;
    }

    public int getTabPosition(String label) {
        return labels.indexOf(label);
    }

    public String getLabel(int position) {
        return labels.get(position);
    }

    public Fragment getFragment(int position, EventDetailPagerAdapter pagerAdapter) {
        switch (position) {
            case 0:
                return getInjector(context).getInstance(DebtsFragment.class).init(pagerAdapter);
            case 1:
                return getInjector(context).getInstance(ExpensesFragment.class).init(pagerAdapter);
            case 2:
                return getInjector(context).getInstance(ParticipantsFragment.class).init(pagerAdapter);
            default:
                throw new IllegalArgumentException("position");
        }
    }

    public int numTabs() {
        return 3;
    }
}
