package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.adapter.UserAdapter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.View.GONE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static roboguice.RoboGuice.getInjector;

public class AddParticipants extends RoboActivity {

    public static final String EVENT_ID = "EVENT_ID";

    @InjectView(R.id.user_grid)
    private GridView userGrid;

    @InjectView(R.id.participant_grid)
    private GridView participantGrid;

    @InjectView(R.id.user_name)
    private EditText userNameField;

    @InjectView(R.id.participant_container)
    private LinearLayout participantContainer;

    @InjectView(R.id.save_button)
    private Button saveButton;

    @Inject
    private EventStore eventStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private ParticipantManager participantManager;

    private String newUserName;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_participants);
        setTitle(R.string.add_event);

        User me = userStore.getUserWithName(sharedPreferenceService.getUserName());
        if (!participantManager.getParticipants().contains(me))
            participantManager.addFixedParticipant(me);

        reloadLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(SOFT_INPUT_STATE_HIDDEN);

        String eventId = getIntent().getStringExtra(EVENT_ID);
        event = eventStore.getById(eventId);
        checkNotNull(event);

        List<User> participants = participantStore.getParticipants(event.getId());
        for (User user : participants) participantManager.addParticipant(user);

        reloadLists();
        userGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                if (user.isNew()) {
                    userStore.persist(user);
                }
                participantManager.addParticipant(user);
                reloadLists();
                clearNewUserName();
            }
        });
        participantGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                participantManager.removeParticipant(user);
                reloadLists();
            }
        });

        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                newUserName = charSequence.toString();
                reloadNonParticipantList();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onSave(View v) {

        participantStore.removeAll(event.getId());
        for (User user : participantManager.getParticipants()) {
            Participant participant = new Participant(user.getId(), event.getId());
            participantStore.persist(participant);
        }

        finish();
    }

    private void clearNewUserName() {
        newUserName = "";
        userNameField.setText("");
    }

    private void enableSearchMode() {
        participantContainer.setVisibility(GONE);
        saveButton.setVisibility(GONE);
    }

    private void disableSearchMode() {
        participantContainer.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
    }

    private void reloadParticipantList() {
        List<User> participants = participantManager.getParticipants();
        UserAdapter participantAdapter = getInjector(this).getInstance(UserAdapter.class);
        participantAdapter.setResBackgroundDrawable(R.drawable.background_user_item_selected);
        participantAdapter.setUsers(participants);
        participantGrid.setAdapter(participantAdapter);
    }

    private void reloadNonParticipantList() {
        List<User> users;
        if (newUserName != null && !newUserName.isEmpty()) {
            enableSearchMode();
            users = userStore.getUsersWithNameLike(newUserName);
            if (userStore.getUserWithName(newUserName) == null) {
                User newUser = new User(newUserName);
                users.add(newUser);
            }
        } else {
            disableSearchMode();
            users = userStore.getAll();
        }

        List<User> nonParticipants = participantManager.filterOutParticipants(users);

        UserAdapter nonParticipantAdapter = getInjector(this).getInstance(UserAdapter.class);
        nonParticipantAdapter.setResBackgroundDrawable(R.drawable.background_user_item);
        nonParticipantAdapter.setUsers(nonParticipants);
        userGrid.setAdapter(nonParticipantAdapter);
    }

    private void reloadLists() {
        reloadParticipantList();
        reloadNonParticipantList();
    }
}
