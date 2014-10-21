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

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.TagStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Currency;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Tag;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.ui.adapter.AttendeeAdapter;
import ch.pantas.billsplitter.ui.adapter.PayerAdapter;
import ch.pantas.billsplitter.ui.adapter.TagAdapter;
import ch.pantas.billsplitter.ui.adapter.TagDeletedListener;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.pantas.billsplitter.services.AmountCalculator.convertToCents;
import static ch.pantas.billsplitter.services.AmountCalculator.convertToString;
import static ch.pantas.billsplitter.services.AmountCalculator.isValidAmount;
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

        tagAdapter.setTagDeletedListener(this);

        String hintAmount = format(getString(R.string.amount_hint, event.getCurrency().getSymbol()));
        amountField.setHint(hintAmount);


        amountField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String amountInput = amountField.getText().toString();
                    amountCents = isValidAmount(amountInput) ? convertToCents(amountInput) : 0;
                    Currency currency = event.getCurrency();
                    amountField.setText(currency.format(amountCents));
                } else {
                    String amountText = amountCents == 0 ? "" : convertToString(amountCents);
                    amountField.setText(amountText);
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

        descriptionField.addTextChangedListener(new TextWatcher() {
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
        });

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


    public void onSave(View v) {
        User payer = payerAdapter.getSelectedUser();
        checkNotNull(payer);

        String description = descriptionField.getText().toString();

        if (amountCents == 0) {
            amountField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        if (expense == null) {
            expense = new Expense(event.getId(), payer.getId(), description, amountCents);
        } else {
            expense.setPayerId(payer.getId());
            expense.setDescription(description);
            expense.setAmount(amountCents);
        }

        expenseStore.persist(expense);

        attendeeStore.removeAll(expense.getId());

        for (User user : attendeeAdapter.getSelectedUsers()) {
            Attendee newAttendee = new Attendee(expense.getId(), user.getId());
            attendeeStore.persist(newAttendee);
        }

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (expense != null) {
            inflater.inflate(R.menu.edit_expense, menu);
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
        amountCents = expense.getAmount();

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

    private void selectAllAttendees() {
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
