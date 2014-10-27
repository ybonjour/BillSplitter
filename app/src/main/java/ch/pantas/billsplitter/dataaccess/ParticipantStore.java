package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkState;

public class ParticipantStore extends BaseStore<Participant> {

    @Inject
    private UserStore userStore;

    @Inject
    public ParticipantStore(ParticipantRowMapper mapper, GenericStore<Participant> genericStore) {
        super(mapper, genericStore);/**/
    }

    public List<Participant> getParticipants(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        List<Participant> participants = genericStore.getModelsByQuery(where);

        return participants;
    }

    public List<Participant> getParticipantsForUsers(String userId) {
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(USER, userId);

        return genericStore.getModelsByQuery(where);
    }

    public Participant getParticipant(String eventId, String userId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(USER, userId);
        List<Participant> participants = genericStore.getModelsByQuery(where);

        checkState(participants.size() <= 1);

        return participants.size() > 0 ? participants.get(0) : null;
    }

    public void removeAll(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        genericStore.removeAll(where);
    }

    public void removeBy(String eventId, String userId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(USER, userId);
        removeAll(where);
    }
}
