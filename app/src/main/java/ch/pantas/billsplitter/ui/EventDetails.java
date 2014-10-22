package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.actions.ActionProvider;
import ch.pantas.billsplitter.ui.actions.AddExpenseAction;
import ch.pantas.billsplitter.ui.actions.DeleteEventAction;
import ch.pantas.billsplitter.ui.actions.EditEventAction;
import ch.pantas.billsplitter.ui.actions.EventDetailsAction;
import ch.pantas.billsplitter.ui.adapter.EventDetailPagerAdapter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import static roboguice.RoboGuice.getInjector;

public class EventDetails extends RoboFragmentActivity {

    public static final String ARGUMENT_EVENT_ID = "event_id";

    @InjectView(R.id.drawer_layout)
    private DrawerLayout drawerLayout;

    @InjectView(R.id.drawer_view)
    private LinearLayout drawerView;

    @InjectView(R.id.left_drawer)
    private ListView drawerList;

    @InjectView(R.id.event_details_pager)
    private ViewPager viewPager;

    @InjectView(R.id.event_details_pager_tab_strip)
    private PagerTabStrip viewPagerTabStrip;

    @Inject
    private EventStore eventStore;

    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private ActionProvider actionProvider;

    private EventDetailPagerAdapter pagerAdapter;

    private Event event;

    private ActionBarDrawerToggle drawerToggle;

    private EventDetailTabs tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.nav_drawer_open_desc,
                R.string.nav_drawer_close_desc
        ) {
            public void onDrawerClosed(View view) {
                setTitle(event.getName());
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                setTitle(R.string.navigation_drawer_title);
                final TextView usernameView = (TextView) findViewById(R.id.nav_drawer_username);
                usernameView.setText(sharedPreferenceService.getUserName());
                invalidateOptionsMenu();
            }
        };

        actionProvider.addEventDetailsAction(R.id.action_add_expense, getInjector(this).getInstance(AddExpenseAction.class));
        actionProvider.addEventDetailsAction(R.id.action_delete_event, getInjector(this).getInstance(DeleteEventAction.class));
        actionProvider.addEventDetailsAction(R.id.action_edit_event, getInjector(this).getInstance(EditEventAction.class));

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();

        sharedPreferenceService.storeActiveEventId(event.getId());
    }

    void init() {
        if (getIntent().hasExtra(ARGUMENT_EVENT_ID)) {
            String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
            event = eventStore.getById(eventId);
        }

        setTitle(event.getName());
        setUpNavigationDrawer();

        tabs = getInjector(this).getInstance(EventDetailTabs.class).init(event);
        pagerAdapter = getInjector(this).getInstance(EventDetailPagerAdapter.class).init(tabs);


        viewPager.setAdapter(pagerAdapter);
        viewPagerTabStrip.setTabIndicatorColorResource(R.color.tab_indicator);

        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        EventDetailsAction action = actionProvider.getEventDetailsAction(item.getItemId());
        if(action != null){
            return action.execute(this);
        }

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerView);

        menu.findItem(R.id.action_add_expense).setVisible(!drawerOpen);
        menu.findItem(R.id.action_delete_event).setVisible(!drawerOpen);
        menu.findItem(R.id.action_edit_event).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    public void setUpNavigationDrawer() {
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        final List<Event> events = eventStore.getAll();

        drawerList.setAdapter(new ArrayAdapter<Event>(this,
                R.layout.drawer_list_item, events));

        int position = events.indexOf(event);
        drawerList.setItemChecked(position, true);

        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ImageView addGroupButton = (ImageView) findViewById(R.id.add_group_button);
        addGroupButton.setClickable(true);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityStarter.startAddEvent(EventDetails.this);
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerLayout.closeDrawer(drawerView);
    }

    public Event getEvent(){
        return event;
    }

    private void selectItem(int position) {
        Event newEvent = eventStore.getAll().get(position);

        drawerList.setItemChecked(position, true);
        activityStarter.startEventDetails(this, newEvent, false);
    }
}