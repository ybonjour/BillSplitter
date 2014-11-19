package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.User;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest extends BaseMockitoInstrumentationTest {

    @Mock
    private UserStore userStore;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private UserService userService;

    @SmallTest
    public void testGetMeReturnsUserFromSharedPreferences() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(sharedPreferenceService.getUserId()).thenReturn(user.getId());
        when(userStore.getById(user.getId())).thenReturn(user);

        // When
        User me = userService.getMe();

        // Then
        assertEquals(user, me);
    }

    @SmallTest
    public void testGetMeReturnsNullIfNoUserIdStored() {
        // Given
        when(sharedPreferenceService.getUserId()).thenReturn(null);

        // When
        User me = userService.getMe();

        // Then
        assertNull(me);
    }

    @SmallTest
    public void testGetMeReturnsNullIfStoredUserDoesNotExist() {
        // Given
        String userId = randomUUID().toString();
        when(sharedPreferenceService.getUserId()).thenReturn(userId);
        when(userStore.getById(userId)).thenReturn(null);

        // When
        User me = userService.getMe();

        // Then
        assertNull(me);
    }

    @SmallTest
    public void testChangeMyUserNameThrowsNullPointerExceptionIfNoUserNameProvided() {
        try {
            userService.changeMyUsername(null);
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testChangeMyUserNameThrowsIllegalArgumentExceptionIfEmptyUserNameProvided() {
        try {
            userService.changeMyUsername("");
            fail("No exception has been thrown.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testChangeMyUserNameThrowsIllegalStateExceptionIfUserIsNotLoggedIn() {
        when(sharedPreferenceService.getUserId()).thenReturn(null);
        try {
            userService.changeMyUsername("Joe");
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testChangeMyUserNameChangesUsernameAndPersists() {
        // Given
        User me = new User(randomUUID().toString(), "Joe");
        when(sharedPreferenceService.getUserId()).thenReturn(me.getId());
        when(userStore.getById(me.getId())).thenReturn(me);
        String newUsername = "Dave";

        // When
        userService.changeMyUsername(newUsername);

        // Then
        assertEquals(newUsername, me.getName());
        verify(userStore, times(1)).persist(me);
    }

    @SmallTest
    public void testFindBestFreeNameForUserThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            userService.findBestFreeNameForUser(null);
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testFindBestFreeNameForUserReturnsUsernameIfNoOtherUserExists() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(userStore.getUserWithName(user.getName())).thenReturn(null);

        // When
        String result = userService.findBestFreeNameForUser(user);

        // Then
        assertEquals(user.getName(), result);
    }

    @SmallTest
    public void testFindBestFreeNameForUserReturnsUserNameIfUserIsPersistedAndOnlyThisUserHasThisName() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(userStore.getUserWithName(user.getName())).thenReturn(user);

        // When
        String result = userService.findBestFreeNameForUser(user);

        // Then
        assertEquals(user.getName(), result);
    }

    @SmallTest
    public void testFindBestFreeNameForUserReturnsNextFreeNameForNewUser() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(userStore.getUserWithName(user.getName())).thenReturn(user);

        User newUser = new User(user.getName());

        // When
        String result = userService.findBestFreeNameForUser(newUser);

        // Then
        assertEquals("Joe 1", result);
    }

    @SmallTest
    public void testFindBestFreeNameForUserReturnsNextFreeNameForPersistedUser() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(userStore.getUserWithName(user.getName())).thenReturn(user);

        User user2 = new User(randomUUID().toString(), user.getName());

        // When
        String result = userService.findBestFreeNameForUser(user2);

        // Then
        assertEquals("Joe 1", result);
    }

    @SmallTest
    public void testFindBestFreeNameIfTwoNamesAreAlreadyTaken() {
        // Given
        User user = new User(randomUUID().toString(), "Joe");
        when(userStore.getUserWithName(user.getName())).thenReturn(user);

        User user2 = new User(randomUUID().toString(), "Joe 1");
        when(userStore.getUserWithName(user2.getName())).thenReturn(user2);

        User user3 = new User(randomUUID().toString(), user.getName());

        // When
        String result = userService.findBestFreeNameForUser(user3);

        // Then
        assertEquals("Joe 2", result);
    }
}
