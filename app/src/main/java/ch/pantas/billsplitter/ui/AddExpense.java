package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.adapter.AttendeeAdapter;
import ch.pantas.billsplitter.ui.adapter.PayerAdapter;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.Integer.parseInt;

public class AddExpense extends RoboActivity {

    public static final String ARGUMENT_EXPENSE_ID = "expense_id";

    @InjectView(R.id.expense_description)
    private EditText descriptionField;

    @InjectView(R.id.expense_amount)
    private EditText amountField;

    @InjectView(R.id.payer_grid)
    private GridView payerGrid;

    @InjectView(R.id.attendees_grid)
    private GridView attendeesGrid;

    @Inject
    private EventStore eventStore;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private PayerAdapter payerAdapter;

    @Inject
    private AttendeeAdapter attendeeAdapter;

    private Event event;
    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
    }

    @Override
    protected void onResume() {
        super.onResume();

        extractDataFromIntent(getIntent());

        setTitle(event.getName());

        if (expense == null) {
            setUpAddScreen();
        } else {
            setUpEditScreen();
        }

        payerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                selectPayer(user);
                selectAllAttendees();
            }
        });

        attendeesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                toggleAttendee(user);
            }
        });
    }

    public void onSave(View v) {
        User payer = payerAdapter.getSelectedUser();
        checkNotNull(payer);

        String description = descriptionField.getText().toString();
        if (description == null || description.isEmpty()) {
            descriptionField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        int amount;
        try {
            amount = parseInt(amountField.getText().toString());
        } catch (NumberFormatException e) {
            amountField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        if (expense == null) {
            expense = new Expense(event.getId(), payer.getId(), description, amount);
        } else {
            expense.setPayerId(payer.getId());
            expense.setDescription(description);
            expense.setAmount(amount);
        }

        expenseStore.persist(expense);

        attendeeStore.removeAll(expense.getId());

        for (User user : attendeeAdapter.getSelectedUsers()) {
            Attendee newAttendee = new Attendee(expense.getId(), user.getId());
            attendeeStore.persist(newAttendee);
        }

        finish();
    }

    private void extractDataFromIntent(Intent intent) {
        checkNotNull(intent);

        String eventId;
        if (intent.hasExtra(ARGUMENT_EVENT_ID)) {
            eventId = intent.getStringExtra(ARGUMENT_EVENT_ID);
        } else if (intent.hasExtra(ARGUMENT_EXPENSE_ID)) {
            String expenseId = intent.getStringExtra(ARGUMENT_EXPENSE_ID);
            expense = expenseStore.getById(expenseId);
            eventId = expense.getEventId();
        } else {
            throw new IllegalStateException("Intent must either have " + ARGUMENT_EVENT_ID + " or " + ARGUMENT_EXPENSE_ID + " set.");
        }

        checkNotNull(eventId);

        event = eventStore.getById(eventId);
    }

    private void setUpEditScreen() {
        checkNotNull(expense);

        descriptionField.setText(expense.getDescription());
        amountField.setText(String.valueOf(expense.getAmount()));

        loadPayerList();
        User payer = userStore.getById(expense.getPayerId());
        selectPayer(payer);

        loadAttendeesList();
        List<User> attendees = attendeeStore.getAttendees(expense.getId());
        for (User user : attendees) {
            attendeeAdapter.select(user);
        }
    }

    private void setUpAddScreen() {
        loadPayerList();
        User me = userStore.getUserWithName(sharedPreferenceService.getUserName());
        checkNotNull(me);
        payerAdapter.select(me);

        loadAttendeesList();
        selectAllAttendees();
    }

    private void toggleAttendee(User user) {
        attendeeAdapter.toggle(user);
        attendeesGrid.invalidateViews();
    }

    private void selectPayer(User user) {
        payerAdapter.select(user);
        payerGrid.invalidateViews();
        loadAttendeesList();
    }

    private void selectAllAttendees(){
        attendeeAdapter.selectAll();
        attendeesGrid.invalidateViews();
    }

    private void loadAttendeesList() {
        List<User> attendees = participantStore.getParticipants(event.getId());
        attendees = payerAdapter.filterOutSelectedUser(attendees);
        attendeeAdapter.setUsers(attendees);
        attendeesGrid.setAdapter(attendeeAdapter);
    }

    private void loadPayerList() {
        payerAdapter.setUsers(participantStore.getParticipants(event.getId()));
        payerGrid.setAdapter(payerAdapter);
    }
}
