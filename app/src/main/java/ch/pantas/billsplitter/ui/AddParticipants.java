package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class AddParticipants extends RoboActivity {

    public static final String ARGUMENT_EXPENSE_ID = "expense_id";

    @InjectView(R.id.participant_name)
    private EditText nameField;

    @InjectView(R.id.participant_list)
    private ListView participantListView;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ExpenseStore expenseStore;

    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_participants);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String expenseId = getIntent().getStringExtra(ARGUMENT_EXPENSE_ID);
        expense = expenseStore.getById(expenseId);
        setTitle(expense.getDescription());
        reloadParticipantList(expense);
    }

    public void onAdd(View view){
        String userName = nameField.getText().toString();
        if (userName == null || userName.isEmpty()) {
            nameField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        User user = userStore.getUserWithName(userName);
        if (user == null) {
            user = new User(userName);
            userStore.persist(user);
        }

        Participant participant = new Participant(expense.getId(), user.getId());
        participantStore.persist(participant);

        reloadParticipantList(expense);
    }

    public void onFinish(View view) {
        finish();
    }

    private void reloadParticipantList(Expense expense){
        List<User> users = participantStore.getParticipants(expense.getId());
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, users);
        participantListView.setAdapter(adapter);
    }
}
