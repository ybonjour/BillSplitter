package ch.pantas.billsplitter.dataaccess;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.AttendeeRowMapper;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.USER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class AttendeeStore extends BaseStore<Attendee> {

    @Inject
    private UserStore userStore;

    @Inject
    public AttendeeStore(AttendeeRowMapper mapper) {
        super(mapper);
    }

    public List<User> getAttendees(String expenseId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EXPENSE, expenseId);
        List<Attendee> attendees = getModelsByQuery(where);
        List<User> users = new LinkedList<User>();
        for (Attendee attendee : attendees) {
            User user = userStore.getById(attendee.getUser());
            if (user != null) users.add(user);
        }

        return users;
    }

    public Attendee getAttendeeByExpenseAndUser(String expenseId, String userId) {
        checkNotNull(expenseId);
        checkArgument(!expenseId.isEmpty());
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EXPENSE, expenseId);
        where.put(USER, userId);
        List<Attendee> attendees = getModelsByQuery(where);
        if (attendees.size() == 0) {
            return null;
        } else {
            return attendees.get(0);
        }
    }
}
