package ch.pantas.billsplitter.model;


import android.content.Context;

import ch.yvu.myapplication.R;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.String.format;

public class ExpensePresentation {

    private final Context context;
    private final User payer;
    private final Expense expense;
    private final Currency currency;

    public ExpensePresentation(User payer, Expense expense, Currency currency, Context context){
        checkNotNull(payer);
        checkNotNull(expense);
        checkNotNull(context);

        this.payer = payer;
        this.expense = expense;
        this.currency = currency;
        this.context = context;
    }

    public Expense getExpense() {
        return expense;
    }

    @Override
    public String toString() {
        String amount = currency.format(expense.getAmount());
        String description = expense.getDescription();
        if(description != null && !description.isEmpty()){
            String template = context.getString(R.string.expense_text_with_description);
            return format(template, payer.getName(), amount, description);
        } else {
            String template = context.getString(R.string.expense_text_without_description);
            return format(template, payer.getName(), amount);
        }
    }
}
