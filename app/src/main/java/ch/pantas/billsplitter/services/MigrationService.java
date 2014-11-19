package ch.pantas.billsplitter.services;

import android.content.Context;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import roboguice.util.RoboAsyncTask;

public class MigrationService {

    @Inject
    private SharedPreferenceService sharedPreferenceService;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private AttendeeStore attendeeStore;
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

    public void migrateToVersion300(){
        addPayerAsAttendeeForAllExpenses();
    }

    private void addPayerAsAttendeeForAllExpenses(){
        List<Expense> expenses = expenseStore.getAll();
        for(Expense expense : expenses){
            Participant payerParticipant = participantStore.getById(expense.getPayerId());

            if(hasAttendeeForPayer(expense, payerParticipant)) continue;

            Attendee attendee = new Attendee(expense.getId(), payerParticipant.getId());
            attendeeStore.persist(attendee);
        }
    }

    private boolean hasAttendeeForPayer(Expense expense, Participant participant) {
        List<Participant> participants = attendeeStore.getAttendingParticipants(expense.getId());
        return participants.contains(participant);
    }

    public void migrateToVersion(final int versionCode, Context context) {
        (new RoboAsyncTask<Void>(context) {
            @Override
            public Void call() throws Exception {
                Integer oldVersionCode = sharedPreferenceService.getCurrentVersionCode();
                // Version Code was introduced in version 300.
                // If no version code exists assume it was last version before 300
                if(oldVersionCode == null) oldVersionCode = 210;

                if(oldVersionCode < 300 && versionCode >= 300) {
                    migrateToVersion300();
                }

                sharedPreferenceService.storeCurrentVersionCode(versionCode);
                return null;
            }
        }).execute();
    }
}