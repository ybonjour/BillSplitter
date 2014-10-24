package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.ExpensePresentation;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.UserItemMode.NORMAL;
import static ch.pantas.billsplitter.ui.adapter.UserItemFormatter.setupUserItem;
import static java.lang.String.format;

public class ExpenseAdapter extends BaseAdapter {

    @Inject
    private LayoutInflater inflater;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    private List<ExpensePresentation> expenses = new LinkedList<ExpensePresentation>();

    public void setExpenses(List<ExpensePresentation> expenses){
        this.expenses = expenses;
    }

    @Override
    public int getCount() {
        return expenses.size();
    }

    @Override
    public Object getItem(int i) {
        return expenses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(R.layout.expense_item, null);
        }

        ExpensePresentation expensePresentation = expenses.get(i);
        Expense expense = expensePresentation.getExpense();

        View userView = view.findViewById(R.id.expense_item_user);
        setupUserItem(userView, expensePresentation.getPayer(), NORMAL);

        String title;
        if(expense.getDescription() != null && !expense.getDescription().isEmpty()){
            String template = view.getResources().getString(R.string.expense_text_with_description);
            title = format(template, expensePresentation.getFormattedAmount(), expense.getDescription());
        } else {
            String template = view.getResources().getString(R.string.expense_text_without_description);
            title = format(template, expensePresentation.getFormattedAmount());
        }

        TextView titleField = (TextView) view.findViewById(R.id.expense_item_title);
        titleField.setText(title);
        titleField.setTextSize(view.getResources().getDimension(R.dimen.expense_item_title));

        TextView textField = (TextView) view.findViewById(R.id.expense_item_text);
        String attendeeTemplate = view.getResources().getString(R.string.expense_attendees);
        textField.setText(format(attendeeTemplate, expensePresentation.getAttendeesCommaSeparated()));
        textField.setTextSize(view.getResources().getDimension(R.dimen.expense_item_text));

        String userId = sharedPreferenceService.getUserId();
        if (!userId.equals(expense.getOwnerId())) {
            view.setAlpha(0.5f);
        }

        return view;
    }
}
