package ch.pantas.billsplitter.services;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.ExpensePresentation;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ExpenseService {

    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private UserStore userStore;
    @Inject
    private EventStore eventStore;
    @Inject
    private Context context;


    public List<ExpensePresentation> getExpensePresentations(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Event event = eventStore.getById(eventId);
        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);

        List<ExpensePresentation> result = new LinkedList<ExpensePresentation>();
        for (Expense expense : expenses) {
            User payer = userStore.getById(expense.getPayerId());
            result.add(new ExpensePresentation(payer, expense, event.getCurrency(), context));
        }

        return result;
    }
}
