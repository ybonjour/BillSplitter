package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.TagStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Tag;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static java.util.Arrays.asList;

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
    private UserService userService;

    @Inject
    private EventStore eventStore;

    @Inject
    private TagStore tagStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(userService.getMe() != null) {
            handleUserLoggedIn();
            return;
        } else if(sharedPreferenceService.getUserName() != null){
            // TODO: Remove once all have 0.2 Migration code to migrate from username to userid
            User me = userStore.getUserWithName(sharedPreferenceService.getUserName());
            if(me != null){
                sharedPreferenceService.storeUserId(me.getId());
                handleUserLoggedIn();
                return;
            }
        }

        setContentView(R.layout.login);
        setTitle(R.string.app_name);
    }

    private void handleUserLoggedIn(){
        Event event = getStoredEvent();
        if (event != null) {
            activityStarter.startEventDetails(this, event, true);
        } else {
            activityStarter.startStartEvent(this);
        }
        finish();
    }

    public void onStarted() {
        String userName = nameField.getText().toString();
        if (userName == null || userName.isEmpty()) {
            nameField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        User me = new User(userName);
        userStore.persist(me);
        sharedPreferenceService.storeUserId(me.getId());
        createStandardTags();

        activityStarter.startStartEvent(this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_login_start == item.getItemId()) {
            onStarted();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createStandardTags() {
        List<Integer> tags = asList(
                R.string.tag_food,
                R.string.tag_drinks,
                R.string.tag_shopping,
                R.string.tag_party,
                R.string.tag_hotel,
                R.string.tag_flight,
                R.string.tag_museum);

        for (int tag : tags) {
            String name = getString(tag);
            if (tagStore.getTagWithName(name) == null) {
                tagStore.persist(new Tag(name));
            }
        }
    }

    private Event getStoredEvent() {
        String eventId = sharedPreferenceService.getActiveEventId();
        if (eventId == null) return null;
        return eventStore.getById(eventId);
    }
}
