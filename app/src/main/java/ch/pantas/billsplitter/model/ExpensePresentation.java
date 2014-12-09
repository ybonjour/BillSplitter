package ch.pantas.billsplitter.model;


import android.content.Context;

import java.util.List;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpensePresentation {
    private final User payer;
    private final Expense expense;
    private final SupportedCurrency currency;
    private final List<User> attendees;

    public ExpensePresentation(User payer, Expense expense, SupportedCurrency currency, List<User> attendees) {
        checkNotNull(payer);
        checkNotNull(expense);
        checkNotNull(attendees);

        this.payer = payer;
        this.expense = expense;
        this.currency = currency;
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
