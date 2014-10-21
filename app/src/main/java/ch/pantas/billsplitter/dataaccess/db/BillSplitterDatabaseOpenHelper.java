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
    private static final int DATABASE_VERSION = 3;

    private final Context context;

    @Inject
    public BillSplitterDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        EventTable.onCreate(db);
        UserTable.onCreate(db);
        ExpenseTable.onCreate(db);
        ParticipantTable.onCreate(db);
        AttendeeTable.onCreate(db);
        TagTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) return;

        EventTable.onUpgrade(db, oldVersion);
        UserTable.onUpgrade(db, oldVersion);
        ExpenseTable.onUpgrade(db, oldVersion);
        ParticipantTable.onUpgrade(db, oldVersion);
        AttendeeTable.onUpgrade(db, oldVersion);
        TagTable.onUpgrade(db, oldVersion);
    }

    public BillSplitterDatabase getDatabase() {
        BillSplitterDatabase db = getInjector(context).getInstance(BillSplitterDatabase.class);
        return db.init(getReadableDatabase());
    }

    public static class Table {
        public static final String ID = "_id";
    }

    public static class EventTable extends Table {
        public static final String TABLE = "Event";


        public static final String NAME = "Name";
        public static final String CURRENCY = "currency";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + CURRENCY + " TEXT, "
                            + NAME + " TEXT);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + CURRENCY + " TEXT;");
                db.execSQL("UPDATE " + TABLE + " SET " + CURRENCY + "='EUR';");
            }
        }
    }

    public static class UserTable {
        public static final String TABLE = "User";

        public static final String ID = "_id";
        public static final String NAME = "Name";


        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + NAME + " TEXT);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
        }
    }

    public static class ExpenseTable extends Table {
        public static final String TABLE = "Expense";

        public static final String EVENT = "event";
        public static final String USER = "user";
        public static final String DESCRIPTION = "description";
        public static final String AMOUNT = "amount";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + EVENT + " TEXT, "
                            + USER + " TEXT, "
                            + DESCRIPTION + " TEXT, "
                            + AMOUNT + " INTEGER);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
        }
    }

    public static class AttendeeTable extends Table {
        public static final String TABLE = "Attendee";

        public static final String EXPENSE = "expense";
        public static final String USER = "user";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + EXPENSE + " TEXT,"
                            + USER + " TEXT);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
        }
    }

    public static class ParticipantTable extends Table {
        public static final String TABLE = "Participant";

        public static final String EVENT = "event";
        public static final String USER = "user";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + EVENT + " TEXT,"
                            + USER + " TEXT);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
        }
    }

    public static class TagTable extends Table {
        public static final String TABLE = "Tag";

        public static final String ID = "_id";
        public static final String NAME = "Name";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + NAME + " TEXT);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
            if (oldVersion < 2) {
                onCreate(db);
            }
        }
    }
}
