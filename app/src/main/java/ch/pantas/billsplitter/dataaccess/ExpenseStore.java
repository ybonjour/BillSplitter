package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.ExpenseRowMapper;
import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.TABLE;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.USER;

public class ExpenseStore extends BaseStore<Expense> {

    @Inject
    public ExpenseStore(ExpenseRowMapper mapper) {
        super(mapper);
    }

    public List<Expense> getExpensesOfEvent(String id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + EVENT + " = '?'";
        return getModelsByQuery(sql, new String[]{id});
    }

    public List<Expense> getExpensesOfUser(String id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + USER + " = '?'";
        return getModelsByQuery(sql, new String[]{id});
    }
}