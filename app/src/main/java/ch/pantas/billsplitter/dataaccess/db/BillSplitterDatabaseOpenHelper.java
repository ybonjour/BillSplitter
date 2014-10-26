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
    private static final int DATABASE_VERSION = 4;

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
        public static final String OWNER = "owner";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + CURRENCY + " TEXT, "
                            + NAME + " TEXT, "
                            + OWNER + " TEXT);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + CURRENCY + " TEXT;");
                db.execSQL("UPDATE " + TABLE + " SET " + CURRENCY + "='EUR';");
            }
            if (oldVersion < 4) {
                // owner is set in migration service
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + OWNER + " TEXT  DEFAULT 'foo';");
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
        public static final String PARTICIPANT = "participant";
        public static final String DESCRIPTION = "description";
        public static final String AMOUNT = "amount";
        public static final String OWNER = "owner";

        private static final String CREATE_SQL =
                "CREATE TABLE " + TABLE + "("
                        + ID + " TEXT PRIMARY KEY, "
                        + EVENT + " TEXT, "
                        + PARTICIPANT + " TEXT, "
                        + DESCRIPTION + " TEXT, "
                        + AMOUNT + " INTEGER, "
                        + OWNER + " TEXT);";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
            if (oldVersion < 4) {
                // Owner is set in MigrationService
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + OWNER + " TEXT DEFAULT 'foo';");

                // Add participant column
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + PARTICIPANT + " TEXT;");

                // Migrate data
                db.execSQL("BEGIN TRANSACTION;");
                db.execSQL("CREATE TABLE tempMig (Expense TEXT, Participant TEXT);");
                db.execSQL("INSERT INTO tempMig SELECT DISTINCT EX._id, P._id FROM Expense EX INNER JOIN Event EV ON EX.event=EV._id INNER JOIN Participant P ON P.event=EV._id AND P.user=EX.user;");
                db.execSQL("UPDATE Expense SET participant=(SELECT Participant FROM tempMig WHERE Expense=Expense._id);");
                db.execSQL("DROP TABLE tempMig;");
                db.execSQL("COMMIT TRANSACTION;");

                // remove user column
                db.execSQL("BEGIN TRANSACTION;");
                db.execSQL("CREATE TABLE tempMig (_id TEXT, event TEXT, user TEXT, description TEXT, amount INTEGER, owner TEXT, participant TEXT);");
                db.execSQL("INSERT INTO tempMig SELECT _id, event, user, description, amount, owner, participant FROM Expense;");
                db.execSQL("DROP TABLE Expense;");
                db.execSQL(CREATE_SQL);
                db.execSQL("INSERT INTO Expense SELECT _id, event, participant, description, amount, owner FROM tempMig;");
                db.execSQL("DROP TABLE tempMig;");
                db.execSQL("COMMIT TRANSACTION;");
            }
        }
    }

    public static class AttendeeTable extends Table {
        public static final String TABLE = "Attendee";

        public static final String EXPENSE = "expense";
        public static final String PARTICIPANT = "participant";

        private static final String CREATE_SQL =
                "CREATE TABLE " + TABLE + "("
                + ID + " TEXT PRIMARY KEY, "
                + EXPENSE + " TEXT,"
                + PARTICIPANT + " TEXT);";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
            if(oldVersion < 4) {
                // Add participant column
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + PARTICIPANT + " TEXT;");

                // Migrate data
                db.execSQL("BEGIN TRANSACTION;");
                db.execSQL("CREATE TABLE tempMig (Attendee TEXT, Participant TEXT);");
                db.execSQL("INSERT INTO tempMig SELECT DISTINCT A._id, P._id FROM Attendee A INNER JOIN Expense EX ON A.expense=EX._id INNER JOIN Event EV ON EX.event=EV._id INNER JOIN Participant P ON P.event=EV._id AND P.user=A.user;\"");
                db.execSQL("UPDATE Attendee SET participant=(SELECT Participant FROM tempMig WHERE Attendee=Attendee._id);");
                db.execSQL("DROP TABLE tempMig;");
                db.execSQL("COMMIT TRANSACTION;");
                // remove user column
                db.execSQL("BEGIN TRANSACTION;");
                db.execSQL("CREATE TABLE tempMig (_id TEXT, expense TEXT, participant TEXT, user TEXT);");
                db.execSQL("INSERT INTO tempMig SELECT _id, expense, participant, user FROM Attendee;");
                db.execSQL("DROP TABLE Attendee;");
                db.execSQL(CREATE_SQL);
                db.execSQL("INSERT INTO Attendee SELECT _id, expense, participant FROM tempMig;");
                db.execSQL("DROP TABLE tempMig;");
                db.execSQL("COMMIT TRANSACTION;");
            }
        }
    }

    public static class ParticipantTable extends Table {
        public static final String TABLE = "Participant";

        public static final String EVENT = "event";
        public static final String USER = "user";
        public static final String CONFIRMED = "confirmed";
        public static final String LAST_UPDATED = "lastUpdated";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE + "("
                            + ID + " TEXT PRIMARY KEY, "
                            + EVENT + " TEXT,"
                            + USER + " TEXT, "
                            + CONFIRMED + " INTEGER, "
                            + LAST_UPDATED + " LONG);"
            );
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion) {
            if (oldVersion < 4) {
                // Value for all participants that are me will be set to true in MigrationService
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + CONFIRMED + " INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + LAST_UPDATED + " LONG DEFAULT 0");
            }
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
