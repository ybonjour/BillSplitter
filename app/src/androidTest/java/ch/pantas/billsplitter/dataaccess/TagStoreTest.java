package ch.pantas.billsplitter.dataaccess;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.TagRowMapper;
import ch.pantas.billsplitter.model.Tag;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.NAME;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TagStoreTest extends BaseStoreTest {
    @Inject
    private TagStore tagStore;

    @Mock
    private TagRowMapper mapper;

    private Tag tag;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tag = new Tag(randomUUID().toString());
    }

    @SmallTest
    public void testGetTagWithNameThrowsNullPointerExceptionIfNoNameProvided() {
        try {
            tagStore.getTagWithName(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetTagWithNameThrowsIllegalArgumentExceptionIfEmptyoNameProvided() {
        try {
            tagStore.getTagWithName("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetTagWithNameReturnsNullIfNoTagWithThatNameExists() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        Tag result = tagStore.getTagWithName("Food");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetTagWithNameReturnsCorrectTag() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(tag);

        // When
        Tag result = tagStore.getTagWithName(tag.getName());

        // Then
        assertEquals(tag, result);
    }

    @SmallTest
    public void testGetTagWithNameHasCorrectWhereArgument() {
        // Given
        String name = "Food";

        // When
        tagStore.getTagWithName(name);

        // Then
        verify(database, times(1)).query(anyString(), argThat(allOf(hasSize(1), hasEntry(NAME, name))));
    }

    @SmallTest
    public void testGetTagWithNameLikeThrowsNullPointerExceptionIfNoNameProvided() {
        try {
            tagStore.getTagWithName(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetTagWithNameLikeReturnsEmptyListIfNoMatchingTagExists() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<Tag> tags = tagStore.getTagsWithNameLike("foo");

        // Then
        assertNotNull(tags);
        assertEquals(0, tags.size());
    }

    @SmallTest
    public void testGetTagWithNameLikeReturnsCorrectTags() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(any(Cursor.class))).thenReturn(tag);

        // When
        List<Tag> tags = tagStore.getTagsWithNameLike("foo");

        // Then
        assertEquals(1, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @SmallTest
    public void testGetTagWithNameLikeHasCorrectWhereArguments() {
        // Given
        String name = "food";
        when(cursor.moveToNext()).thenReturn(false);

        // When
        tagStore.getTagsWithNameLike(name);

        // Then
        verify(database, times(1)).queryWithLike(anyString(), argThat(allOf(hasSize(1), hasEntry(NAME, name))));
    }
}