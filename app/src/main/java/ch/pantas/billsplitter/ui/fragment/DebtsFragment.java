package ch.pantas.billsplitter.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.pantas.billsplitter.services.ExpenseService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.adapter.DebtAdapter;
import ch.pantas.splitty.R;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static roboguice.RoboGuice.getInjector;

public class DebtsFragment extends BaseEventDetailsFragment {

    @Inject
    private DebtCalculator debtCalculator;

    @Inject
    private UserService userService;

    @Inject
    private ExpenseService expenseService;

    @Inject
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Event event = getCurrentEvent();
        checkNotNull(event);

        View rootView = inflater.inflate(R.layout.fragment_debts_list, container, false);
        setUpDebtsList(rootView, event);
        return rootView;
    }

    private void setUpDebtsList(final View rootView, final Event event) {
        final ListView list = (ListView) rootView.findViewById(R.id.debts_list);
        List<Debt> debts = debtCalculator.calculateDebts(event);
        DebtAdapter adapter = getInjector(context).getInstance(DebtAdapter.class);
        adapter.setDebts(debts);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Debt debt = (Debt) adapterView.getItemAtPosition(position);
                User me = userService.getMe();

                if (!me.equals(debt.getTo())) return;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.mark_debt_as_paid)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                expenseService.createPaybackExpense(debt, getCurrentEvent());
                                getPagerAdapter().notifyDataSetChanged();
                                list.invalidateViews();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setTitle(R.string.mark_debt_as_paid_title);

                builder.create().show();
            }
        });
    }
}
