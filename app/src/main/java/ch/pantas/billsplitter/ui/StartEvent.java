package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.inject.Inject;

import ch.pantas.billsplitter.services.ActivityStarter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;

public class StartEvent extends RoboActivity {

    @Inject
    private ActivityStarter activityStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_event);
        setTitle(R.string.events_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_add_event == item.getItemId()) {
            activityStarter.startAddEvent(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
