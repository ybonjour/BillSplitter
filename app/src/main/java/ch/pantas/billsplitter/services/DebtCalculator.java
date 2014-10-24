package ch.pantas.billsplitter.services;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class DebtCalculator {
    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private DebtOptimizer debtOptimizer;


    public List<Debt> calculateDebts(Event event) {
        checkNotNull(event);

        List<Debt> debts = getDebts(event);
        return debtOptimizer.optimize(debts, event.getCurrency());
    }

    private List<Debt> getDebts(Event event) {
        List<Debt> debts = new LinkedList<Debt>();
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        for (Expense expense : expenses) {
            Participant toParticipant = participantStore.getById(expense.getPayerId());
            List<Participant> fromParticipants = attendeeStore.getAttendees(expense.getId());
            int amount = expense.getAmount() / (fromParticipants.size() + 1);
            for (Participant fromParticipant : fromParticipants) {
                User fromUser = userStore.getById(fromParticipant.getUserId());
                User toUser = userStore.getById(toParticipant.getUserId());
                debts.add(new Debt(fromUser, toUser, amount, event.getCurrency()));
            }
        }

        return debts;
    }

}
