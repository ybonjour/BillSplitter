package ch.pantas.billsplitter.dataaccess.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.Map;

import static android.util.Pair.create;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkState;

public class BillSplitterDatabase {

    private SQLiteDatabase database;

    public BillSplitterDatabase init(SQLiteDatabase database) {
        checkNotNull(database);

        this.database = database;
        return this;
    }

    public Cursor query(String table, Map<String, String> where) {
        checkNotNull(database, "BillSplitterDatabase must be initialized with a SQLiteDatabase");

        Pair<String, String[]> selection = createSelection(where);
        return database.query(table, null, selection.first, selection.second, null, null, null);
    }

    public void insert(String table, ContentValues values) {
        checkNotNull(database, "BillSplitterDatabase must be initialized with a SQLiteDatabase");

        long retVal = database.insert(table, null, values);
        checkState(retVal != -1, "Row could not be inserted into " + table);
    }

    public void update(String table, ContentValues values) {
        checkNotNull(table);
        checkArgument(!table.isEmpty());
        checkNotNull(values);
        checkArgument(values.containsKey(ID));

        String where = ID + " = ?";
        String[] whereArguments = new String[]{ values.getAsString(ID) };

        int rowAffected = database.update(table, values, where, whereArguments);
        checkState(rowAffected == 1, "Not exactly one row updated.");
    }

    private static Pair<String, String[]> createSelection(Map<String, String> whereClause) {
        if (whereClause == null) return create(null, null);

        StringBuffer selection = new StringBuffer();
        String[] selectionArgs = new String[whereClause.size()];

        int i = 0;

        for (String column : whereClause.keySet()) {
            Object value = whereClause.get(column);
            selection.append(column + "= ? ");
            selectionArgs[i] = value.toString();
            i += 1;
        }

        return create(selection.toString(), selectionArgs);
    }
}
