package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.UserRowMapper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserStoreTest extends BaseMockitoInstrumentationTest {

    private static final List<User> EMPTY_LIST = new LinkedList<User>();

    @Mock
    private User user;

    @Mock
    private UserRowMapper mapper;

    @Mock
    private GenericStore<User> genericStore;

    @Inject
    private UserStore store;


    @Override
    protected Module getDefaultModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<GenericStore<User>>() {
                }).toInstance(genericStore);
            }
        };
    }

    @SmallTest
    public void testGetUserWithNameThrowsNullPointerExceptionIfNoNameProvided() {
        try {
            store.getUserWithName(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetUserWithNameThrowsIllegalArgumentExceptionIfEmptyNameProvided() {
        try {
            store.getUserWithName("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetUserWithNameReturnsNullIfNoUserWithThatNameExists() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        User result = store.getUserWithName("Joe");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetUserWithNameReturnsUserIfItExists() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(user));

        // When
        User result = store.getUserWithName("Joe");

        // Then
        assertEquals(user, result);
    }


    @SmallTest
    public void testGetUserWithNameHasCorrectWhereArgument() {
        // Given
        String name = "Joe";

        // When
        store.getUserWithName(name);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(NAME, name))));
    }

    @SmallTest
    public void testGetUsersWithNameLikeThrowsNullPointerExceptionIfNoNameProvided() {
        try {
            store.getUsersWithNameLike(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetUserWithNameLikeReturnsResultOfGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<User> users = store.getUsersWithNameLike("A");

        // Then
        assertEquals(EMPTY_LIST, users);
    }

    @SmallTest
    public void testGetUserWithNameLikeSendsQueryWithCorrectWhereClause() {
        // Given
        String nameFilter = "A";

        // When
        store.getUsersWithNameLike(nameFilter);

        // Then
        verify(genericStore, times(1)).getModelsByQueryWithLike(argThat(allOf(hasSize(1), hasEntry(NAME, nameFilter))));
    }
}
