package ch.pantas.billsplitter.model;


import android.app.Application;
import android.content.Context;

import com.google.inject.Inject;

import ch.yvu.myapplication.R;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.String.format;

public class ExpensePresentation {

    private final Context context;
    private final User payer;
    private final Expense expense;

    public ExpensePresentation(User payer, Expense expense, Context context){
        checkNotNull(payer);
        checkNotNull(expense);
        checkNotNull(context);

        this.payer = payer;
        this.expense = expense;
        this.context = context;
    }

    public Expense getExpense() {
        return expense;
    }

    @Override
    public String toString() {
        String template = context.getString(R.string.expenseText);
        double displayedAmount = expense.getAmount() / 100.0;
        return format(template, payer.getName(), displayedAmount, expense.getDescription());
    }
}
