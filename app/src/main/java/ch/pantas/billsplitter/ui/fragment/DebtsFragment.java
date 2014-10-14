package ch.pantas.billsplitter.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.yvu.myapplication.R;
import roboguice.fragment.RoboFragment;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class DebtsFragment extends RoboFragment {

    private Event event;

    @Inject
    private DebtCalculator debtCalculator;

    public DebtsFragment init(Event event) {
        checkNotNull(event);
        this.event = event;

        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkNotNull(event);

        View rootView = inflater.inflate(R.layout.fragment_debts_list, container, false);
        ListView list = (ListView) rootView.findViewById(R.id.debts_list);

        List<Debt> debts = debtCalculator.calculateDebts(event);
        ArrayAdapter<Debt> adapter = new ArrayAdapter<Debt>(rootView.getContext(), android.R.layout.simple_list_item_1, debts);
        list.setAdapter(adapter);

        return rootView;
    }
}
