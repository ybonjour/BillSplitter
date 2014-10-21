package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;

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

    public void onAddEvent(View view) {
        activityStarter.startAddEvent(this);
    }
}
