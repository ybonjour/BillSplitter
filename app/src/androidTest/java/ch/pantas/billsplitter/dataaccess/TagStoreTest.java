package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.TagRowMapper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Tag;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.NAME;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TagStoreTest extends BaseMockitoInstrumentationTest {

    private static final List<Tag> EMPTY_LIST = new LinkedList<Tag>();

    @Mock
    private GenericStore<Tag> genericStore;

    @Mock
    private TagRowMapper mapper;
    @Inject
    private TagStore store;

    private Tag tag;

    @Override
    protected Module getDefaultModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<GenericStore<Tag>>() {
                }).toInstance(genericStore);
            }
        };
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tag = new Tag(randomUUID(), "testTag");
    }

    @SmallTest
    public void testGetTagWithNameThrowsNullPointerExceptionIfNoNameProvided() {
        try {
            store.getTagWithName(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetTagWithNameThrowsIllegalArgumentExceptionIfEmptyNameProvided() {
        try {
            store.getTagWithName("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetTagWithNameReturnsNullIfNoTagWithThatNameExists() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        Tag result = store.getTagWithName("Food");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetTagWithNameReturnsCorrectTag() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(tag));

        // When
        Tag result = store.getTagWithName(tag.getName());

        // Then
        assertEquals(tag, result);
    }

    @SmallTest
    public void testGetTagWithNameHasCorrectWhereArgument() {
        // Given
        String name = "Food";
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getTagWithName(name);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(NAME, name))));
    }

    @SmallTest
    public void testGetTagWithNameLikeThrowsNullPointerExceptionIfNoNameProvided() {
        try {
            store.getTagWithName(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetTagWithNameLikeReturnsResultOfGenericStore() {
        // Given
        when(genericStore.getModelsByQueryWithLike(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Tag> tags = store.getTagsWithNameLike("foo");

        // Then
        assertEquals(EMPTY_LIST, tags);
    }

    @SmallTest
    public void testGetTagWithNameLikeHasCorrectWhereArguments() {
        // Given
        String name = "food";

        // When
        store.getTagsWithNameLike(name);

        // Then
        verify(genericStore, times(1)).getModelsByQueryWithLike(argThat(allOf(hasSize(1), hasEntry(NAME, name))));
    }
}