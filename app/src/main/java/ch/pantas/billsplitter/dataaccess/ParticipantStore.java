package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ParticipantStore extends BaseStore<Participant> {

    @Inject
    private UserStore userStore;

    @Inject
    public ParticipantStore(ParticipantRowMapper mapper) {
        super(mapper);
    }

    public List<Participant> getParticipants(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        List<Participant> participants = getModelsByQuery(where);

        return participants;
    }

    public Participant getParticipant(String eventId, String userId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(USER, userId);
        List<Participant> participants = getModelsByQuery(where);

        checkArgument(participants.size()<=1);

        return participants.size() > 0 ? participants.get(0) : null;
    }

    public List<Participant> getParticipantsForUsers(String userId){
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(USER, userId);

        return getModelsByQuery(where);
    }

    public void removeAll(String eventId) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        removeAll(where);
    }

    public void removeBy(String eventId, String userId){
        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(USER, userId);
        removeAll(where);
    }
}
