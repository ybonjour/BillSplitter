package ch.pantas.billsplitter.services;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class SharedPreferenceService {

    public static final String USER_NAME = "USER_NAME";

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
}
