package ch.pantas.billsplitter;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.splitty.R;
import roboguice.RoboGuice;

import static roboguice.RoboGuice.getInjector;

public class BillSplitterApplication extends Application {

    private Map<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.billsplitter_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferenceService sharedPreferenceService = getInjector(this).getInstance(SharedPreferenceService.class);
        boolean trackingEnabled = sharedPreferenceService.getTrackingEnabled();
        GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(!trackingEnabled);
    }

    public enum TrackerName {
        APP_TRACKER
    }
}
