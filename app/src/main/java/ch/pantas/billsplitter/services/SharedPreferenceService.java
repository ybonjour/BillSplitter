package ch.pantas.billsplitter.services;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.UUID;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class SharedPreferenceService {

    public static final String USER_ID = "USE_ID";
    public static final String ACTIVE_EVENT_ID = "ACTIVE_EVENT_ID";
    public static final String TRACKING_ENABLED = "TRACKING_ENABLED";
    public static final String CURRENT_VERSION_CODE = "CURRENT_VERSION_CODE";

    @Inject
    private SharedPreferences preferences;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    public UUID getUserId() {
        try{
            return UUID.fromString(preferences.getString(USER_ID, ""));
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    public void storeUserId(UUID userId) {
        checkNotNull(userId);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, userId.toString());
        editor.apply();
    }

    public void storeActiveEventId(UUID eventId) {
        SharedPreferences.Editor editor = preferences.edit();
        if (eventId == null) editor.putString(ACTIVE_EVENT_ID, null);
        else editor.putString(ACTIVE_EVENT_ID, eventId.toString());
        editor.apply();
    }

    public UUID getActiveEventId() {
        try{
            return UUID.fromString(preferences.getString(ACTIVE_EVENT_ID, ""));
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    public Integer getCurrentVersionCode() {
        if (!preferences.contains(CURRENT_VERSION_CODE)) return null;
        return preferences.getInt(CURRENT_VERSION_CODE, -1);
    }

    public void storeCurrentVersionCode(int versionCode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_VERSION_CODE, versionCode);
        editor.apply();
    }

    public void registerTrackingEnabledListener(final TrackingEnabledListener trackingEnabledListener) {
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

    public void unregisterTrackingEnabledListener() {
        if (sharedPreferenceChangeListener != null) {
            preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            sharedPreferenceChangeListener = null;
        }
    }

    public boolean getTrackingEnabled() {
        return preferences.getBoolean(TRACKING_ENABLED, false);
    }

    public interface TrackingEnabledListener {
        void onTrackingEnabledChanged(boolean trackingEnabled);
    }
}
