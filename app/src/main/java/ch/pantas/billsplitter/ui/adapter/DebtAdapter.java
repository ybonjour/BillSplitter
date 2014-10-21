package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.Debt;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.setupUserItem;

public class DebtAdapter extends BaseAdapter {
    @Inject
    private LayoutInflater inflater;

    private List<Debt> debts = new LinkedList<Debt>();

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    @Override
    public int getCount() {
        return debts.size();
    }

    @Override
    public Object getItem(int i) {
        return debts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.debt_item, null);
        }

        Debt debt = debts.get(i);

        View userFromRootView = view.findViewById(R.id.debt_item_user_from);
        setupUserItem(userFromRootView, debt.getFrom(), R.drawable.background_user_item_selected);

        View userToRootView = view.findViewById(R.id.debt_item_user_to);
        setupUserItem(userToRootView, debt.getTo(), R.drawable.background_user_item_selected);

        TextView text = (TextView) view.findViewById(R.id.debt_item_amount);
        text.setText(debt.getFormattedAmount());
        text.setTextSize(view.getResources().getDimension(R.dimen.debt_amount_textsize));


        return view;
    }

}
