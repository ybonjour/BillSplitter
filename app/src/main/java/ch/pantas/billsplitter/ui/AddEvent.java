package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.inject.Inject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.ui.adapter.UserAdapter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static roboguice.RoboGuice.getInjector;

public class AddEvent extends RoboActivity {

    @InjectView(R.id.event_name)
    private EditText eventNameField;

    @InjectView(R.id.user_grid)
    private GridView userGrid;

    @Inject
    private EventStore eventStore;

    @Inject
    private UserStore userStore;

    private Set<String> selectedUsers = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        setTitle(R.string.add_event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadUserList();
        userGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                boolean selected = toggleUserSelected(user);
                LinearLayout container = (LinearLayout) view.findViewById(R.id.user_item_container);
                int background = selected ?
                        R.drawable.background_user_item_selected :
                        R.drawable.background_user_item;
                container.setBackground(getResources().getDrawable(background));
            }
        });
    }

    public void onSave(View v) {
        String eventName = eventNameField.getText().toString();
        if (eventName.isEmpty()) {
            eventNameField.setBackgroundColor(getResources().getColor(R.color.error_color));
        } else {
            Event event = new Event(eventName);
            eventStore.persist(event);

            finish();
        }
    }

    private boolean toggleUserSelected(User user){
        if(selectedUsers.contains(user.getId())) {
            selectedUsers.remove(user.getId());
            return false;
        } else {
            selectedUsers.add(user.getId());
            return true;
        }
    }

    private void reloadUserList() {
        List<User> users = userStore.getAll();
        UserAdapter userAdapter = getInjector(this).getInstance(UserAdapter.class);
        userAdapter.init(users);
        userGrid.setAdapter(userAdapter);
    }
}
