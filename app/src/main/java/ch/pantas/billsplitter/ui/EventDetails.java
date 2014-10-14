package ch.pantas.billsplitter.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.ui.adapter.EventDetailPagerAdapter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import static android.app.ActionBar.NAVIGATION_MODE_TABS;
import static roboguice.RoboGuice.getInjector;

public class EventDetails extends RoboFragmentActivity {

    public static final String ARGUMENT_EVENT_ID = "event_id";

    @InjectView(R.id.event_details_pager)
    private ViewPager viewPager;

    @Inject
    private EventStore eventStore;

    @Inject
    private ActivityStarter activityStarter;

    private EventDetailPagerAdapter pagerAdapter;

    private Event event;

    private EventDetailTabs tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        if (getIntent().hasExtra(ARGUMENT_EVENT_ID)) {
            String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
            event = eventStore.getById(eventId);
        }

        setTitle(event.getName());

        tabs = getInjector(this).getInstance(EventDetailTabs.class).init(event);

        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(NAVIGATION_MODE_TABS);

        pagerAdapter = getInjector(this).getInstance(EventDetailPagerAdapter.class).init(tabs);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        ActionBar.TabListener listener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };
        actionBar.removeAllTabs();
        for (int i = 0; i < tabs.numTabs(); i++) {
            actionBar.addTab(actionBar.newTab().setText(tabs.getLabel(i)).setTabListener(listener));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_add == item.getItemId()) {
            activityStarter.startAddExpense(this, event);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}