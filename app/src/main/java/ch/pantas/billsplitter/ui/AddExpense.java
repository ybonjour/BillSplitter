package ch.pantas.billsplitter.ui;

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

import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.Double.parseDouble;

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
        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        if (eventId != null) {
            // Add expense
            event = eventStore.getById(eventId);
            setTitle(event.getName());

            loadPayerList();
            loadAttendeesList();
        }

        String expenseId = getIntent().getStringExtra(ARGUMENT_EXPENSE_ID);
        if (expenseId != null) {
            // Edit expense
            expense = expenseStore.getById(expenseId);
            eventId = expense.getEventId();
            event = eventStore.getById(eventId);
            setTitle(event.getName());

            // Initialize text fields
            descriptionField.setText(expense.getDescription());
            amountField.setText(String.valueOf(expense.getAmount()));

            // Normally initialize payer and attendee list
            loadPayerList();

            // Select saved payer
            User payer = userStore.getById(expense.getPayerId());
            selectPayer(payer);

            // Select already saved attendees
            List<User> attendees = attendeeStore.getAttendees(expenseId);
            attendeeAdapter.deselectAll();
            for (User user : attendees) {
                attendeeAdapter.select(user);
            }
        }

        payerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                selectPayer(user);
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

        double amount;
        try {
            amount = parseDouble(amountField.getText().toString());
        } catch (NumberFormatException e) {
            amountField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        if (expense == null) {
            // New expense
            expense = new Expense(event.getId(), payer.getId(), description, amount);
        }
        else {
            // Edit existing expense
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

    private void toggleAttendee(User user) {
        attendeeAdapter.toggle(user);
        attendeesGrid.invalidateViews();
    }

    private void selectPayer(User user) {
        payerAdapter.select(user);
        payerGrid.invalidateViews();
        loadAttendeesList();
    }

    private void loadAttendeesList() {
        List<User> attendees = participantStore.getParticipants(event.getId());
        attendees = payerAdapter.filterOutSelectedUser(attendees);
        attendeeAdapter.setUsers(attendees);
        attendeeAdapter.selectAll();
        attendeesGrid.setAdapter(attendeeAdapter);
    }

    private void loadPayerList() {
        payerAdapter.setUsers(participantStore.getParticipants(event.getId()));
        User me = userStore.getUserWithName(sharedPreferenceService.getUserName());
        checkNotNull(me);
        payerAdapter.select(me);
        payerGrid.setAdapter(payerAdapter);
    }
}
