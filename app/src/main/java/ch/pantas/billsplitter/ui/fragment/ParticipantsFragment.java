package ch.pantas.billsplitter.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.ParticipantManager;
import ch.pantas.billsplitter.ui.adapter.UserAdapter;
import ch.pantas.splitty.R;

import static android.view.View.GONE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static roboguice.RoboGuice.getInjector;

public class ParticipantsFragment extends BaseEventDetailsFragment {

    @Inject
    private UserStore userStore;
    @Inject
    private UserService userService;
    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private ParticipantManager participantManager;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private Context context;

    private EditText userField;
    private GridView participantGrid;
    private GridView userGrid;
    private LinearLayout participantContainer;

    private String newUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Event event = getCurrentEvent();
        setupParticipantManager(event);

        View rootView = inflater.inflate(R.layout.fragment_participants, container, false);
        loadFields(rootView);
        reloadLists();

        userGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                if (user.isNew()) {
                    userStore.persist(user);
                }
                participantManager.addParticipant(user);

                storeParticipants(event);
                reloadLists();
                clearNewUserName();
            }
        });
        participantGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);

                Participant  participant = participantStore.getParticipant(event.getId(), user.getId());
                checkNotNull(participant);

                if (canBeRemoved(participant)) {
                    participantManager.removeParticipant(user);
                    participantStore.removeBy(event.getId(), user.getId());
                } else {
                    makeText(context, getString(R.string.error_remove_user), LENGTH_LONG).show();
                }
                reloadLists();
            }
        });

        userField.addTextChangedListener(new TextWatcher() {
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


        return rootView;
    }

    private boolean isParticipantInvolvedInExpense(Expense expense, Participant participant) {
        if (participant.isNew()) return false;
        if (expense.getPayerId().equals(participant.getId())) return true;
        List<Participant> attendees = attendeeStore.getAttendees(expense.getId());
        return attendees.contains(participant);
    }

    private boolean isParticipantInvolvedInExpensesOfEvent(Event event, Participant participant) {
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        for (Expense expense : expenses) {
            if (isParticipantInvolvedInExpense(expense, participant)) {
                return true;
            }
        }

        return false;
    }

    private boolean canBeRemoved(Participant participant) {
        if (participant.isNew()) return true;

        User user = userStore.getById(participant.getUserId());

        return !participantManager.isFixedParticipant(user) && !isParticipantInvolvedInExpensesOfEvent(getCurrentEvent(), participant);

    }

    private void setupParticipantManager(Event event) {
        participantManager.clear();
        User me = userService.getMe();
        participantManager.addFixedParticipant(me);

        List<Participant> participants = participantStore.getParticipants(event.getId());
        for (Participant participant : participants) {
            User user = userStore.getById(participant.getUserId());
            participantManager.addParticipant(user);
        }
    }

    private void loadFields(View rootView) {
        userField = (EditText) rootView.findViewById(R.id.user_name);
        participantGrid = (GridView) rootView.findViewById(R.id.participant_grid);
        userGrid = (GridView) rootView.findViewById(R.id.user_grid);
        participantContainer = (LinearLayout) rootView.findViewById(R.id.participant_container);
    }

    private void clearNewUserName() {
        newUserName = "";
        userField.setText("");
    }

    private void storeParticipants(Event event) {
        for (User user : participantManager.getParticipants()) {
            Participant participant = participantStore.getParticipant(event.getId(), user.getId());
            if (participant == null) {
                participantStore.persist(new Participant(user.getId(), event.getId()));
            }
        }

        for (Participant participant : participantStore.getParticipants(event.getId())) {
            User user = userStore.getById(participant.getUserId());
            if (!participantManager.getParticipants().contains(user)) {
                participantStore.removeBy(event.getId(), user.getId());
            }
        }
    }

    private void enableSearchMode() {
        participantContainer.setVisibility(GONE);
    }

    private void disableSearchMode() {
        participantContainer.setVisibility(View.VISIBLE);
    }

    private void reloadParticipantList() {
        List<User> participants = participantManager.getParticipants();
        UserAdapter participantAdapter = getInjector(context).getInstance(UserAdapter.class);
        participantAdapter.setUserItemMode(SELECTED);
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

        UserAdapter nonParticipantAdapter = getInjector(context).getInstance(UserAdapter.class);
        nonParticipantAdapter.setUserItemMode(UNSELECTED);
        nonParticipantAdapter.setUsers(nonParticipants);
        userGrid.setAdapter(nonParticipantAdapter);
    }

    private void reloadLists() {
        reloadParticipantList();
        reloadNonParticipantList();
    }
}
