package ch.pantas.billsplitter.services;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class SharedPreferenceService {

    public static final String USER_NAME = "USER_NAME";
    public static final String ACTIVE_EVENT_ID = "ACTIVE_EVENT_ID";
    public static final String TRACKING_ENABLED = "TRACKING_ENABLED";

    @Inject
    private SharedPreferences preferences;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    public void storeUserName(String userName) {
        checkNotNull(userName);
        checkArgument(!userName.isEmpty());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    public String getUserName() {
        return preferences.getString(USER_NAME, null);
    }

    public void storeActiveEventId(String eventId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACTIVE_EVENT_ID, eventId);
        editor.apply();
    }

    public String getActiveEventId() { return preferences.getString(ACTIVE_EVENT_ID, null); }

    public void registerTrackingEnabledListener(final TrackingEnabledListener trackingEnabledListener){
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                if (key.equals(TRACKING_ENABLED)) {
                    trackingEnabledListener.onTrackingEnabledChanged(preferences.getBoolean(TRACKING_ENABLED, false));
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterTrackingEnabledListener(){
        if(sharedPreferenceChangeListener != null){
            preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            sharedPreferenceChangeListener = null;
        }
    }

    public boolean getTrackingEnabled(){
        return preferences.getBoolean(TRACKING_ENABLED, false);
    }

    public interface TrackingEnabledListener{
        void onTrackingEnabledChanged(boolean trackingEnabled);
    }
}
