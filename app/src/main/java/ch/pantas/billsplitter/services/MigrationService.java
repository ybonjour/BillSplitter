package ch.pantas.billsplitter.services;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

public class MigrationService {

    @Inject
    private SharedPreferenceService sharedPreferenceService;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    @Inject
    private EventStore eventStore;

    public void migrateToVersion200(){
        String username = sharedPreferenceService.getUserName();
        if(username == null) return;

        User me = userStore.getUserWithName(username);
        if(me == null) return;

        sharedPreferenceService.storeUserId(me.getId());


        List<Participant> participants = participantStore.getParticipantsForUsers(me.getId());
        for(Participant participant : participants){
            participant.setConfirmed(true);
            participantStore.persist(participant);
        }

        List<Event> events = eventStore.getAll();
        for(Event event : events){
            event.setOwnerId(me.getId());
            eventStore.persist(event);
        }

        List<Expense> expenses = expenseStore.getAll();
        for(Expense expense : expenses){
            expense.setOwnerId(me.getId());
            expenseStore.persist(expense);
        }


    }
}