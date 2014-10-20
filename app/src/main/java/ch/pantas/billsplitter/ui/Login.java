package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import javax.inject.Inject;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class Login extends RoboActivity {

    @InjectView(R.id.user_name)
    private EditText nameField;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private UserStore userStore;

    @Inject
    private EventStore eventStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (sharedPreferenceService.getUserName() != null) {
            Event event = getStoredEvent();
            if (event != null) {
                activityStarter.startEventList(this);
                activityStarter.startEventDetails(this, event);
            } else {
                activityStarter.startEventList(this);
            }
            finish();
            return;
        }

        setContentView(R.layout.login);
        setTitle(R.string.set_user_name);
    }

    public void onSave(View view) {
        String userName = nameField.getText().toString();
        if (userName == null || userName.isEmpty()) {
            nameField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        userStore.persist(new User(userName));
        sharedPreferenceService.storeUserName(userName);

        activityStarter.startEventList(this);
        finish();
    }

    private Event getStoredEvent() {
        String eventId = sharedPreferenceService.getActiveEventId();
        if (eventId == null) return null;
        return eventStore.getById(eventId);
    }
}
