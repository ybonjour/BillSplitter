package ch.pantas.billsplitter.dataaccess.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}
