package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.UserRowMapper;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;

@Singleton
public class UserStore extends BaseStore<User> {

    @Inject
    public UserStore(UserRowMapper mapper) {
        super(mapper);
    }

    public User getUserWithName(String name) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        List<User> users = getModelsByQuery(where);

        if(users.size() == 0){
            return null;
        } else {
            return users.get(0);
        }
    }
}
