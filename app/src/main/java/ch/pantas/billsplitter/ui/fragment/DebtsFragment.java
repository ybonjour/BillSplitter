package ch.pantas.billsplitter.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.pantas.billsplitter.ui.adapter.DebtAdapter;
import ch.pantas.splitty.R;

import static roboguice.RoboGuice.getInjector;

public class DebtsFragment extends BaseEventDetailsFragment {

    @Inject
    private DebtCalculator debtCalculator;

    @Inject
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Event event = getCurrentEvent();

        View rootView = inflater.inflate(R.layout.fragment_debts_list, container, false);
        setUpDebtsList(rootView, event);
        return rootView;
    }

    private void setUpDebtsList(View rootView, Event event) {
        ListView list = (ListView) rootView.findViewById(R.id.debts_list);
        List<Debt> debts = debtCalculator.calculateDebts(event);
        DebtAdapter adapter = getInjector(context).getInstance(DebtAdapter.class);
        adapter.setDebts(debts);
        list.setAdapter(adapter);
    }
}
