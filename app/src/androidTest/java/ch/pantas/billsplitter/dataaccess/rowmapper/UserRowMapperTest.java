package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserRowMapperTest extends BaseMockitoInstrumentationTest {
    @Inject
    private UserRowMapper mapper;

    @SmallTest
    public void testMapThrowsNullPointerExceptionIfNoCursorProvided() {
        try {
            mapper.map(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testMapCorrectlyMapsCursor() {
        // Given
        int id = 2;
        String name = "Joe";
        Cursor c = createUserCursor(id, name);

        // When
        User user = mapper.map(c);

        // Then
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
    }

    private Cursor createUserCursor(int id, String name){
        Cursor c = mock(Cursor.class);
        when(c.getColumnIndex(ID)).thenReturn(0);
        when(c.getInt(0)).thenReturn(id);
        when(c.getColumnIndex(NAME)).thenReturn(1);
        when(c.getString(1)).thenReturn(name);

        return c;
    }

}
