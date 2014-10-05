package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ParticipantStore extends BaseStore<Participant> {

    @Inject
    private UserStore userStore;

    @Inject
    public ParticipantStore(ParticipantRowMapper mapper) {
        super(mapper);
    }

    public List<User> getParticipants(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        List<Participant> participants = getModelsByQuery(where);
        List<User> users = new LinkedList<User>();
        for (Participant participant : participants) {
            User user = userStore.getById(participant.getUserId());
            if(user == null) continue;
            users.add(user);
        }

        return users;
    }
}
