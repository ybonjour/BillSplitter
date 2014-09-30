package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;

import java.util.Map;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.UserRowMapper;
import ch.pantas.billsplitter.model.User;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.UserTable.NAME;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserStoreTest extends BaseStoreTest {

    @Inject
    private UserStore store;

    @Mock
    private User user;

    @Mock
    private UserRowMapper mapper;


    @SmallTest
    public void testGetUserWithNameReturnsNullIfNoUserWithThatNameExists(){
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        User result = store.getUserWithName("Joe");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetUserWithNameReturnsUserIfItExists(){
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(user);

        // When
        User result = store.getUserWithName("Joe");

        // Then
        assertEquals(user, result);
    }

    @SmallTest
    public void testGetUserWithNameHasCorrectWhereArgument(){
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        String name = "Joe";

        // When
        store.getUserWithName(name);

        // Then
        verify(database, times(1)).query(anyString(), argThat(allOf(hasSize(1), hasEntry(NAME, name))));
    }

    private static Matcher<Map<String, String>> hasSize(final int size){
        return new TypeSafeMatcher<Map<String, String>>() {
            @Override
            public boolean matchesSafely(Map<String, String> kvMap) {
                return kvMap.size() == size;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("map with size ");
                description.appendValue(size);
            }
        };
    }



}
