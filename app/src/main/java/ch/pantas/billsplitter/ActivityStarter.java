package ch.pantas.billsplitter;

import android.content.Context;
import android.content.Intent;

import com.google.inject.Singleton;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.ui.AddEvent;
import ch.pantas.billsplitter.ui.AddExpense;
import ch.pantas.billsplitter.ui.ExpensesList;

import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ActivityStarter {

    public void startAddEvent(Context context) {
        checkNotNull(context);

        Intent intent = new Intent(context, AddEvent.class);
        context.startActivity(intent);
    }

    public void startExpensesList(Context context, Event event) {
        checkNotNull(context);
        checkNotNull(event);

        Intent intent = new Intent(context, ExpensesList.class);
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        context.startActivity(intent);
    }

    public void startAddExpense(Context context, Event event) {
        Intent intent = new Intent(context, AddExpense.class);
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        context.startActivity(intent);
    }
}