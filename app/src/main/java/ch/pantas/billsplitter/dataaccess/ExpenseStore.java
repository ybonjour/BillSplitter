package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.ExpenseRowMapper;
import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.OWNER;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ExpenseStore extends BaseStore<Expense> {

    @Inject
    public ExpenseStore(ExpenseRowMapper mapper, GenericStore<Expense> genericStore) {
        super(mapper, genericStore);
    }

    public List<Expense> getExpensesOfEvent(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        return genericStore.getModelsByQuery(where);
    }

    public List<Expense> getExpensesOfEvent(String eventId, String ownerId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(ownerId);
        checkArgument(!ownerId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(OWNER, ownerId);
        return genericStore.getModelsByQuery(where);
    }

    public void removeAll(String eventId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        removeAll(where);
    }

    public void removeAll(String eventId, String ownerId) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(ownerId);
        checkArgument(!ownerId.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(OWNER, ownerId);
        removeAll(where);
    }
}