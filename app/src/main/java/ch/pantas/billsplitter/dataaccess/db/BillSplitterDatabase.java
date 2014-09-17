package ch.pantas.billsplitter.dataaccess.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class BillSplitterDatabase {

    private SQLiteDatabase database;

    public BillSplitterDatabase init(SQLiteDatabase database) {
        checkNotNull(database);

        this.database = database;
        return this;
    }

    public Cursor rawQuery(String sql, String[] selectionArguments) {
        checkNotNull(database, "BillSplitterDatabase must be initialized with a SQLiteDatabase");

        return database.rawQuery(sql, selectionArguments);
    }

    public void insert(String table, ContentValues values) {
        checkNotNull(database, "BillSplitterDatabase must be initialized with a SQLiteDatabase");

        long retVal = database.insert(table, null, values);
        checkArgument(retVal != -1, "Row could not be inserted into " + table);
    }
}
