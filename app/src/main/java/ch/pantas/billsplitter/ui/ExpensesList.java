package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.ActivityStarter;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpensesList extends RoboActivity {

    public static final String ARGUMENT_EVENT_ID = "event_id";

    @InjectView(R.id.expensesListView)
    private ListView expensesListView;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private EventStore eventStore;

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
        reloadList(event);
    }

    private void reloadList(Event event){
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        ArrayAdapter<Expense> adapter = new ArrayAdapter<Expense>(this, android.R.layout.simple_list_item_1, expenses);
        expensesListView.setAdapter(adapter);
    }

    public void onAddExpense(View view) {
        activityStarter.startAddExpense(this, event);
    }

}
