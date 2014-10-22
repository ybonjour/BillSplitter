package ch.pantas.billsplitter.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.ExpensePresentation;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.ExpenseService;
import ch.pantas.billsplitter.ui.adapter.ExpenseAdapter;
import ch.pantas.splitty.R;

import static roboguice.RoboGuice.getInjector;

public class ExpensesFragment extends BaseEventDetailsFragment {
    @Inject
    private ActivityStarter activityStarter;
    @Inject
    private ExpenseService expenseService;
    @Inject
    private Context context;

    private ListView expensesList;

    @Override
    public void onResume() {
        super.onResume();
        expensesList.invalidateViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Event event = getCurrentEvent();
        View rootView = inflater.inflate(R.layout.fragment_expenses_list, container, false);
        expensesList = (ListView) rootView.findViewById(R.id.expenses_list);

        List<ExpensePresentation> expenses = expenseService.getExpensePresentations(event.getId());
        ExpenseAdapter adapter = getInjector(context).getInstance(ExpenseAdapter.class);
        adapter.setExpenses(expenses);
        expensesList.setAdapter(adapter);

        expensesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ExpensePresentation expensePresentation = (ExpensePresentation) adapterView.getItemAtPosition(i);
                activityStarter.startEditExpense(context, expensePresentation.getExpense());
            }
        });

        return rootView;
    }
}
