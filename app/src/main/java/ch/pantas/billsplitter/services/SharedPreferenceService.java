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

    @Inject
    private SharedPreferences preferences;

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
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACTIVE_EVENT_ID, eventId);
        editor.apply();
    }

    public String getActiveEventId() { return preferences.getString(ACTIVE_EVENT_ID, null); }
}
