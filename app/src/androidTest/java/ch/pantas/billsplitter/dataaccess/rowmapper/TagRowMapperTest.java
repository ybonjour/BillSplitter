package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.UUID;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Tag;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.NAME;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TagRowMapperTest extends BaseMockitoInstrumentationTest {

    @Inject
    private TagRowMapper mapper;

    @SmallTest
    public void testMapThrowsNullPointerExceptionIfNoCursorProvided() {
        try {
            mapper.map(null);
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testMapMapsTagCorrectly() {
        // Given
        String id = randomUUID().toString();
        String name = "food";
        Cursor c = createTagCursor(id, name);

        // When
        Tag tag = mapper.map(c);

        // Then
        assertNotNull(tag);
        assertEquals(id, tag.getId().toString());
        assertEquals(name, tag.getName());
    }

    @SmallTest
    public void testGetValuesThrowsNullPointerExceptionIfTagIsNull() {
        try {
            mapper.getValues(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetValuesReturnsCorrectValues() {
        // Given
        UUID id = randomUUID();
        String name = "Food";
        Tag tag = new Tag(id, name);

        // When
        ContentValues values = mapper.getValues(tag);

        // Then
        assertNotNull(values);
        assertEquals(id.toString(), values.get(ID));
        assertEquals(name, values.get(NAME));
    }

    private Cursor createTagCursor(String id, String name) {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(ID)).thenReturn(0);
        when(cursor.getString(0)).thenReturn(id);
        when(cursor.getColumnIndex(NAME)).thenReturn(1);
        when(cursor.getString(1)).thenReturn(name);

        return cursor;

    }
}
