package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.model.Expense;
import ch.yvu.myapplication.R;
import roboguice.activity.RoboActivity;

public class ExpensesList extends RoboActivity {

    @Inject
    private ExpenseStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);


        String eventId = getIntent().getStringExtra("event_id");

        List<Expense> expenses = store.getExpensesOfEvent(eventId);
        ListView expenseListView = (ListView) findViewById(R.id.expensesListView);
        ArrayAdapter<Expense> adapter = new ArrayAdapter<Expense>(this, android.R.layout.simple_list_item_1, expenses);
        expenseListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expenses_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
