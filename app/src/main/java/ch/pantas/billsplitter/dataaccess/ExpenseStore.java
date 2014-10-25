package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.ExpenseRowMapper;
import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.OWNER;

public class ExpenseStore extends BaseStore<Expense> {

    @Inject
    public ExpenseStore(ExpenseRowMapper mapper) {
        super(mapper);
    }

    public List<Expense> getExpensesOfEvent(String eventId) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        return getModelsByQuery(where);
    }

    public List<Expense> getExpensesOfEvent(String eventId, String ownerId) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(OWNER, ownerId);
        return getModelsByQuery(where);
    }

    public void removeAll(String eventId) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        removeAll(where);
    }

    public void removeAll(String eventId, String ownerId) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(EVENT, eventId);
        where.put(OWNER, ownerId);
        removeAll(where);
    }
}