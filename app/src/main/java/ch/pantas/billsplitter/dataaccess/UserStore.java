package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.UserRowMapper;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.TABLE;

@Singleton
public class UserStore extends BaseStore<User> {

    @Inject
    public UserStore(UserRowMapper mapper) {
        super(mapper);
    }

    public List<User> getUsersWithNameLike(String name) {
        String sql = "SELECT * FROM " + TABLE + " WHERE " + NAME + " LIKE ?";
        return getModelsByQuery(sql, new String[]{name});
    }
}
