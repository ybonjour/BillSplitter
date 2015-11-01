package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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

    private List<Pair<Participant, User>> users;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private UserStore userStore;

    @Inject
    private UserService userService;

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
        // FIXME: Load user name, mac (maybe ID?)
        newUser = createNewConnectedUser("TestUser", "TestMac");

        final User me = userService.getMe();
        List<Participant> participants = participantStore.getParticipants(eventId);
        users = from(participants)
                .transform(new Function<Participant, Pair<Participant, User>>() {
                    @Override
                    public Pair<Participant, User> apply(Participant input) {
                        return new Pair(input, userStore.getById(input.getUserId()));
                    }
                })
                .filter(new Predicate<Pair<Participant, User>>() {
                    @Override
                    public boolean apply(Pair<Participant, User> input) {
                        return !input.second.isConnectedUser() &&
                                !input.second.getId().equals(me.getId());
                    }
                })
                .toList();

        List<User> shownList = from(users).transform(new Function<Pair<Participant, User>, User>() {
                @Override
                public User apply(Pair<Participant, User> input) {
                    return input.second;
                }
            }).toList();
        participantsList.setAdapter(new ArrayAdapter<User>(this,
                android.R.layout.simple_list_item_1, shownList));

        participantsList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replaceParticipantWithConnectedUser(users.get(position).first);
                finish();
            }
        });

        Button addNewUser = (Button) findViewById(R.id.action_create_user);
        addNewUser.setOnClickListener(new View.OnClickListener() {
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
