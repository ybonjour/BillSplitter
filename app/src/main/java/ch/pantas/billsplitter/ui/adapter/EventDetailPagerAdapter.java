package ch.pantas.billsplitter.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.inject.Inject;

import ch.pantas.billsplitter.ui.EventDetailTabs;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventDetailPagerAdapter extends FragmentPagerAdapter {

    private EventDetailTabs tabs;

    public EventDetailPagerAdapter init(EventDetailTabs tabs){
        checkNotNull(tabs);

        this.tabs = tabs;

        return this;
    }

    @Inject
    public EventDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.getFragment(position);
    }

    @Override
    public int getCount() {
        return tabs.numTabs();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
