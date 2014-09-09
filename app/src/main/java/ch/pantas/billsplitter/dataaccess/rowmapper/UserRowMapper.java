package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;

import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User map(Cursor cursor) {
        checkNotNull(cursor);

        int idIdx = cursor.getColumnIndex(ID);
        int nameIdx = cursor.getColumnIndex(NAME);

        String id = cursor.getString(idIdx);
        String name = cursor.getString(nameIdx);

        return new User(id, name);
    }
}
