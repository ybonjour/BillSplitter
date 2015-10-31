package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static com.google.common.collect.FluentIterable.from;

public class AddOrReplaceParticipant extends RoboActivity {

    public static final String EVENT_ID = "EVENT_ID";

    @InjectView(R.id.participant_list)
    private ListView participantsList;

    private List<Participant> participants;
    private List<User> users;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ActivityStarter activityStarter;

    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.add_or_replace_participant);
        setContentView(R.layout.add_or_replace_participant);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final UUID eventId = (UUID) getIntent().getSerializableExtra(EVENT_ID);
        participants = participantStore.getParticipants(eventId);

        // FIXME: Load user name, mac (maybe ID?)
        newUser = createNewConnectedUser("TestUser", "TestMac");

        users = from(participants)
            .transform(new Function<Participant, User>() {
                @Override
                public User apply(Participant input) {
                    return userStore.getById(input.getUserId());
                }
            })
            .toList();

        participantsList.setAdapter(new ArrayAdapter<User>(this,
                android.R.layout.simple_list_item_1, users));

        participantsList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replaceParticipantWithConnectedUser(participants.get(position));
                finish();
            }
        });

        Button addGroup = (Button) findViewById(R.id.action_create_user);
        addGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNewParticipant(eventId);
                finish();
            }
        });
    }

    private User createNewConnectedUser(String username, String macAddress) {
        User user = new User(username, macAddress);
        userStore.persist(user);
        return user;
    }

    private void createNewParticipant(UUID eventId) {
        Participant participant = new Participant(newUser.getId(), eventId, true, 0);
        participantStore.persist(participant);
    }

    private void replaceParticipantWithConnectedUser(Participant participant) {
        participant.setUserId(newUser.getId());
        participantStore.persist(participant);
    }
}
