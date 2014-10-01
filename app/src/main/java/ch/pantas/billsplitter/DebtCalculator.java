package ch.pantas.billsplitter;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

public class DebtCalculator {
    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ParticipantStore participantStore;

    public List<Debt> calculateDebts(Event event) {
        List<Debt> debts = new LinkedList<Debt>();
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        for (Expense expense : expenses) {
            User toUser = userStore.getById(expense.getPayerId());
            List<User> fromUsers = participantStore.getParticipants(expense.getId());
            double amount = expense.getAmount() / (fromUsers.size() + 1);
            for (User fromUser : fromUsers) {
                debts.add(new Debt(fromUser, toUser, amount));
            }
        }

        return debts;
    }

}
