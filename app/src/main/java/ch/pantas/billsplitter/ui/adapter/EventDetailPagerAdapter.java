package ch.pantas.billsplitter.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.ui.EventDetailTabs;
import ch.pantas.billsplitter.ui.fragment.BaseEventDetailsFragment;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventDetailPagerAdapter extends FragmentPagerAdapter {

    private EventDetailTabs tabs;
    private FragmentManager fragmentManager;

    @Inject
    public EventDetailPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
    }

    public EventDetailPagerAdapter init(EventDetailTabs tabs){
        checkNotNull(tabs);

        this.tabs = tabs;

        if(fragmentManager != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if(fragments != null) {
                for (Fragment fragment : fragments) {
                    if (!(fragment instanceof BaseEventDetailsFragment)) continue;
                    BaseEventDetailsFragment eventFragment = (BaseEventDetailsFragment) fragment;
                    eventFragment.setPagerAdapter(this);
                }
            }
        }

        return this;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.getFragment(position, this);
    }

    @Override
    public int getCount() {
        return tabs.numTabs();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle (int position) {
        return tabs.getLabel(position);
    }
}
