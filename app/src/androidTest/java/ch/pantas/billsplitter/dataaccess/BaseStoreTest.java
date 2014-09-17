package ch.pantas.billsplitter.dataaccess;

import android.database.Cursor;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class BaseStoreTest extends BaseMockitoInstrumentationTest {
    @Mock
    private BillSplitterDatabaseOpenHelper databaseHelper;

    @Mock
    private BillSplitterDatabase database;

    @Mock
    protected Cursor cursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        when(databaseHelper.getDatabase()).thenReturn(database);
        when(database.rawQuery(anyString(), any(String[].class))).thenReturn(cursor);
    }
}