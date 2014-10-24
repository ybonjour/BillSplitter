package ch.pantas.billsplitter.services;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.ExpensePresentation;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ExpenseService {

    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private UserStore userStore;
    @Inject
    private EventStore eventStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private Context context;


    public List<ExpensePresentation> getExpensePresentations(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Event event = eventStore.getById(eventId);
        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);

        List<ExpensePresentation> result = new LinkedList<ExpensePresentation>();
        for (Expense expense : expenses) {
            Participant payer = participantStore.getById(expense.getPayerId());
            List<Participant> attendees = attendeeStore.getAttendees(expense.getId());

            User payingUser = userStore.getById(payer.getUserId());
            List<User> attendingUsers = new LinkedList<User>();
            for (Participant participant : attendees) {
                User user = userStore.getById(participant.getUserId());
                attendingUsers.add(user);
            }
            result.add(new ExpensePresentation(payingUser, expense, event.getCurrency(), attendingUsers, context));
        }

        return result;
    }
}
