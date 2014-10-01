package ch.pantas.billsplitter.dataaccess;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ParticipantStore extends BaseStore<Participant> {

    @Inject
    private UserStore userStore;

    @Inject
    public ParticipantStore(ParticipantRowMapper mapper) {
        super(mapper);
    }

    public List<User> getParticipants(String expenseId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EXPENSE, expenseId);
        List<Participant> participants = getModelsByQuery(where);
        List<User> users = new LinkedList<User>();
        for (Participant participant : participants) {
            User user = userStore.getById(participant.getUser());
            if (user != null) users.add(user);
        }

        return users;
    }

    public Participant getParticipantByExpenseAndUser(String expenseId, String userId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EXPENSE, expenseId);
        where.put(USER, userId);
        List<Participant> participants = getModelsByQuery(where);
        if (participants.size() == 0) {
            return null;
        } else {
            return participants.get(0);
        }
    }
}
