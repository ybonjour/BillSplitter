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
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.ParticipantManager;
import ch.pantas.billsplitter.ui.adapter.UserAdapter;
import ch.pantas.splitty.R;

import static android.view.View.GONE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.SELECTED;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.UNSELECTED;
import static roboguice.RoboGuice.getInjector;

public class ParticipantsFragment extends BaseEventDetailsFragment {

    @Inject
    private UserStore userStore;
    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private SharedPreferenceService sharedPreferenceService;
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
                if (canBeRemoved(user)) {
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

    private boolean isUserInvolvedInExpense(Expense expense, User user) {
        if (user.isNew()) return false;
        if (expense.getPayerId().equals(user.getId())) return true;
        List<User> attendees = attendeeStore.getAttendees(expense.getId());
        return attendees.contains(user);
    }

    private boolean isUserInvolvedInExpensesOfEvent(Event event, User user) {
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        for (Expense expense : expenses) {
            if (isUserInvolvedInExpense(expense, user)) {
                return true;
            }
        }

        return false;
    }

    private boolean canBeRemoved(User user) {
        if (user.isNew()) return true;

        return !participantManager.isFixedParticipant(user) && !isUserInvolvedInExpensesOfEvent(getCurrentEvent(), user);

    }

    private void setupParticipantManager(Event event) {
        participantManager.clear();
        User me = userStore.getUserWithName(sharedPreferenceService.getUserName());
        participantManager.addFixedParticipant(me);

        List<User> participants = participantStore.getParticipants(event.getId());
        for (User user : participants) {
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
        participantStore.removeAll(event.getId());
        for (User user : participantManager.getParticipants()) {
            Participant participant = new Participant(user.getId(), event.getId());
            participantStore.persist(participant);
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
