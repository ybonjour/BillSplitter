package ch.pantas.billsplitter.dataaccess.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static roboguice.RoboGuice.getInjector;

@Singleton
public class BillSplitterDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BillSplitter";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    @Inject
    public BillSplitterDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EventTable.CREATE);
        db.execSQL(UserTable.CREATE);
        db.execSQL(AttendanceTable.CREATE);
        //createDummyEvent(db);
    }

    private static void createDummyEvent(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + EventTable.TABLE);
        db.execSQL("INSERT INTO " + EventTable.TABLE + "(" + EventTable.NAME + ") VALUES(?)", new String[] { "Event 1" });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public BillSplitterDatabase getDatabase(){
        BillSplitterDatabase db = getInjector(context).getInstance(BillSplitterDatabase.class);
        return db.init(getReadableDatabase());
    }

    public class EventTable {
        public static final String TABLE = "Event";

        public static final String ID = "_id";
        public static final String NAME = "Name";

        static final String CREATE = "CREATE TABLE " + TABLE + "("
                + ID + " TEXT PRIMARY KEY, "
                + NAME + " TEXT);";
    }

    public class UserTable {
        public static final String TABLE = "User";

        public static final String ID = "_id";
        public static final String NAME = "Name";

        static final String CREATE = "CREATE TABLE " + TABLE + "("
                + ID + " TEXT PRIMARY KEY, "
                + NAME + " TEXT);";
    }

    public class AttendanceTable {
        public static final String TABLE = "Attendance";

        public static final String ID = "_id";
        public static final String USER = "user";
        public static final String EVENT = "event";

        static final String CREATE = "CREATE TABLE " + TABLE + "("
                + ID + " TEXT PRIMARY KEY, "
                + USER + " TEXT, "
                + EVENT + " TEXT);";
    }
}
