package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.BillSplitterApplication;
import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.TagStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Tag;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.adapter.AttendeeAdapter;
import ch.pantas.billsplitter.ui.adapter.PayerAdapter;
import ch.pantas.billsplitter.ui.adapter.TagAdapter;
import ch.pantas.billsplitter.ui.adapter.TagDeletedListener;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.pantas.billsplitter.services.AmountCalculator.convertToCents;
import static ch.pantas.billsplitter.services.AmountCalculator.convertToString;
import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.String.format;

public class AddExpense extends RoboActivity implements TagDeletedListener {

    public static final String ARGUMENT_EXPENSE_ID = "expense_id";

    @InjectView(R.id.expense_description)
    private EditText descriptionField;

    @InjectView(R.id.tag_grid)
    private GridView tagGrid;

    @InjectView(R.id.tag_grid_container)
    private LinearLayout tagGridContainer;

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
    private UserService userService;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private TagStore tagStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private PayerAdapter payerAdapter;

    @Inject
    private AttendeeAdapter attendeeAdapter;

    @Inject
    private TagAdapter tagAdapter;

    private Event event;
    private Expense expense;
    private int amountCents = 0;
    private TextWatcher descriptionTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
        Tracker tracker = ((BillSplitterApplication) getApplication()).getTracker(
                BillSplitterApplication.TrackerName.APP_TRACKER);

        tracker.setScreenName("ch.pantas.billsplitter.ui.AddExpense");

        tracker.send(new HitBuilders.AppViewBuilder().build());
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

        tagAdapter.setTagDeletedListener(this);

        String hintAmount = format(getString(R.string.amount_hint, event.getCurrency().getSymbol()));
        amountField.setHint(hintAmount);


        amountField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String amountInput = amountField.getText().toString();
                    amountCents = convertToCents(amountInput);
                    SupportedCurrency currency = event.getCurrency();
                    amountField.setText(currency.format(amountCents));
                } else {
                    amountField.setText(convertToString(amountCents));
                }
            }
        });

        descriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String tag = descriptionField.getText().toString();
                    loadTags(tag);
                    tagGridContainer.setVisibility(VISIBLE);
                } else {
                    tagGridContainer.setVisibility(GONE);
                }
            }
        });

        descriptionTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String tag = descriptionField.getText().toString();
                loadTags(tag);
                tagGridContainer.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        descriptionField.addTextChangedListener(descriptionTextWatcher);

        tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getId() == R.id.tag_item_delete) {
                    Toast.makeText(AddExpense.this, "Juhuuuu", Toast.LENGTH_LONG).show();
                }
                Tag tag = (Tag) adapterView.getItemAtPosition(i);
                tagStore.persist(tag);
                descriptionField.setText(tag.getName());
                tagGridContainer.setVisibility(GONE);
            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();
        amountField.setOnFocusChangeListener(null);
        descriptionField.setOnFocusChangeListener(null);
        descriptionField.removeTextChangedListener(descriptionTextWatcher);
        tagGrid.setOnItemClickListener(null);
        payerGrid.setOnItemClickListener(null);
        attendeesGrid.setOnItemClickListener(null);
    }

    private void loadTags(String tag) {
        Tag existingTag;
        List<Tag> tags;
        if (tag == null || tag.isEmpty()) {
            tags = tagStore.getAll();
        } else {
            tags = tagStore.getTagsWithNameLike(tag);
            existingTag = tagStore.getTagWithName(tag);
            if (existingTag == null) {
                tags.add(new Tag(tag));
            }
        }

        tagAdapter.setTags(tags);
        tagGrid.setAdapter(tagAdapter);
    }


    public void onSave() {
        User payingUser = payerAdapter.getSelectedUser();
        Participant payer = participantStore.getParticipant(event.getId(), payingUser.getId());
        checkNotNull(payer);

        String description = descriptionField.getText().toString();

        // Make sure we take the value of the amount field if it still has focus
        // (in that case, the amountCents variable has not yet been updated)
        if (amountField.hasFocus()) {
            amountCents = convertToCents(amountField.getText().toString());
        }

        if (amountCents == 0) {
            amountField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        if (expense == null) {
            expense = new Expense(event.getId(), payer.getId(), description, amountCents, sharedPreferenceService.getUserId());
        } else {
            expense.setPayerId(payer.getId());
            expense.setDescription(description);
            expense.setAmount(amountCents);
        }

        expenseStore.persist(expense);

        attendeeStore.removeAll(expense.getId());

        for (User user : attendeeAdapter.getSelectedUsers()) {
            Participant participant = participantStore.getParticipant(event.getId(), user.getId());
            checkNotNull(participant);

            Attendee newAttendee = new Attendee(expense.getId(), participant.getId());
            attendeeStore.persist(newAttendee);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (expense != null) {
            inflater.inflate(R.menu.edit_expense, menu);
        } else {
            inflater.inflate(R.menu.add_expense, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_delete_expense == item.getItemId()) {
            if (expense == null) return true;
            attendeeStore.removeAll(expense.getId());
            expenseStore.removeById(expense.getId());
            finish();
            return true;
        } else if (R.id.action_save_expense == item.getItemId()) {
            onSave();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTagDelete(Tag tag) {
        tagStore.removeById(tag.getId());
        loadTags(descriptionField.getText().toString());
    }

    private void extractDataFromIntent(Intent intent) {
        checkNotNull(intent);

        UUID eventId;
        if (intent.hasExtra(ARGUMENT_EVENT_ID)) {
            eventId = (UUID) intent.getSerializableExtra(ARGUMENT_EVENT_ID);
        } else if (intent.hasExtra(ARGUMENT_EXPENSE_ID)) {
            UUID expenseId = (UUID) intent.getSerializableExtra(ARGUMENT_EXPENSE_ID);
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
        amountCents = expense.getAmount();

        loadPayerList();
        Participant participantPayer = participantStore.getById(expense.getPayerId());
        User payer = userStore.getById(participantPayer.getUserId());
        selectPayer(payer);

        loadAttendeesList();
        List<Participant> attendees = attendeeStore.getAttendingParticipants(expense.getId());
        for (Participant participant : attendees) {
            User user = userStore.getById(participant.getUserId());
            attendeeAdapter.select(user);
        }
    }

    private void setUpAddScreen() {
        loadPayerList();
        User me = userService.getMe();
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

    private void selectAllAttendees() {
        attendeeAdapter.selectAll();
        attendeesGrid.invalidateViews();
    }

    private void loadAttendeesList() {
        List<Participant> attendees = participantStore.getParticipants(event.getId());

        List<User> attendingUsers = new LinkedList<User>();
        for (Participant participant : attendees) {
            attendingUsers.add(userStore.getById(participant.getUserId()));
        }

        attendeeAdapter.setUsers(attendingUsers);
        attendeesGrid.setAdapter(attendeeAdapter);
    }

    private void loadPayerList() {
        List<Participant> participants = participantStore.getParticipants(event.getId());
        List<User> users = new LinkedList<User>();
        for (Participant participant : participants) {
            users.add(userStore.getById(participant.getUserId()));
        }

        payerAdapter.setUsers(users);
        payerGrid.setAdapter(payerAdapter);
    }
}
