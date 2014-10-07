package ch.pantas.billsplitter.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpensesList extends RoboActivity {

    public static final String ARGUMENT_EVENT_ID = "event_id";

    @InjectView(R.id.expenses_list)
    private ListView expensesList;

    @InjectView(R.id.debts_list)
    private ListView debtsList;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private EventStore eventStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private DebtCalculator debtCalculator;

    @Inject
    private ActivityStarter activityStarter;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String eventId = getIntent().getStringExtra(ARGUMENT_EVENT_ID);
        event = eventStore.getById(eventId);
        checkNotNull(event);
        setTitle(event.getName());
        reloadExpensesList(event);
        reloadDebtsList(event);

        expensesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Expense expense = (Expense) expensesList.getAdapter().getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(adapterView.getContext());
                builder.setTitle("Edit or delete this expense?");

                builder.setMessage(expense.toString());
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activityStarter.startEditExpense(ExpensesList.this, expense);
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        expenseStore.remove(expense.getId());
                        attendeeStore.removeAll(expense.getId());
                        reloadExpensesList(event);
                        reloadDebtsList(event);
                    }
                });
                builder.show();
                }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        expensesList.setOnItemClickListener(null);
    }

    private void reloadExpensesList(Event event){
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        ArrayAdapter<Expense> adapter = new ArrayAdapter<Expense>(this, android.R.layout.simple_list_item_1, expenses);
        expensesList.setAdapter(adapter);
    }

    private void reloadDebtsList(Event event) {
        List<Debt> debts = debtCalculator.calculateDebts(event);
        ArrayAdapter<Debt> adapter = new ArrayAdapter<Debt>(this, android.R.layout.simple_list_item_1, debts);
        debtsList.setAdapter(adapter);
    }

    public void onAddExpense(View view) {
        activityStarter.startAddExpense(this, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expenses_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_edit_event:
                activityStarter.startEditEvent(this, event);
                return true;
            case R.id.action_delete_event:
                // Delete event, participants of event, attendees of all expenses, all expenses
                eventStore.removeAll(event.getId());
                List<Expense> expensesOfEvent = expenseStore.getExpensesOfEvent(event.getId());
                for (Expense e : expensesOfEvent) {
                    attendeeStore.removeAll(e.getId());
                }
                expenseStore.removeAll(event.getId());
                participantStore.removeAll(event.getId());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
