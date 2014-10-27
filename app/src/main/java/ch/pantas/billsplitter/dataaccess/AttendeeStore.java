package ch.pantas.billsplitter.dataaccess;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.AttendeeRowMapper;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class AttendeeStore extends BaseStore<Attendee> {

    @Inject
    private ParticipantStore participantStore;

    @Inject
    public AttendeeStore(AttendeeRowMapper mapper, GenericStore<Attendee> genericStore) {
        super(mapper, genericStore);
    }

    public List<Attendee> getAttendees(String expenseId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EXPENSE, expenseId);
        return genericStore.getModelsByQuery(where);
    }

    public List<Participant> getAttendingParticipants(String expenseId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());

        List<Attendee> attendees = getAttendees(expenseId);
        List<Participant> participants = new LinkedList<Participant>();
        for (Attendee attendee : attendees) {
            Participant participant = participantStore.getById(attendee.getParticipant());
            if (participant != null) participants.add(participant);
        }

        return participants;
    }

    public void removeAll(String expenseId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EXPENSE, expenseId);
        genericStore.removeAll(where);
    }
}
