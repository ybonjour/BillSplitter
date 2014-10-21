package ch.pantas.billsplitter.model;


import android.content.Context;

import java.util.List;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpensePresentation {

    private final Context context;
    private final User payer;
    private final Expense expense;
    private final Currency currency;
    private final List<User> attendees;

    public ExpensePresentation(User payer, Expense expense, Currency currency, List<User> attendees, Context context) {
        checkNotNull(payer);
        checkNotNull(expense);
        checkNotNull(attendees);
        checkNotNull(context);

        this.payer = payer;
        this.expense = expense;
        this.currency = currency;
        this.context = context;
        this.attendees = attendees;
    }

    public Expense getExpense() {
        return expense;
    }

    public User getPayer() {
        return payer;
    }

    public String getFormattedAmount() {
        return currency.format(expense.getAmount());
    }

    public String getAttendeesCommaSeparated() {
        StringBuffer attendeeString = new StringBuffer();
        boolean first = true;
        for (User user : attendees) {
            if (!first) {
                attendeeString.append(", ");
            }

            attendeeString.append(user.getName());
            first = false;
        }

        return attendeeString.toString();
    }
}
