package ch.pantas.billsplitter.services;

import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;

import static ch.pantas.billsplitter.services.SharedPreferenceService.ACTIVE_EVENT_ID;
import static ch.pantas.billsplitter.services.SharedPreferenceService.TRACKING_ENABLED;
import static ch.pantas.billsplitter.services.SharedPreferenceService.TrackingEnabledListener;
import static ch.pantas.billsplitter.services.SharedPreferenceService.USER_ID;
import static ch.pantas.billsplitter.services.SharedPreferenceService.USER_NAME;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SharedPreferenceServiceTest extends BaseMockitoInstrumentationTest {

    @Inject
    private SharedPreferenceService service;

    @Inject
    private SharedPreferences sharedPreferences;

    private String cachedUsername;
    private String cachedUserId;
    private String cachedActiveEventId;
    private boolean cachedTrackingEnabled;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        cachedUsername = sharedPreferences.getString(USER_NAME, null);
        cachedUserId = sharedPreferences.getString(USER_ID, null);
        cachedActiveEventId = sharedPreferences.getString(ACTIVE_EVENT_ID, null);
        boolean cachedTrackingEnabled = sharedPreferences.getBoolean(TRACKING_ENABLED, false);

        sharedPreferences.edit().remove(USER_NAME).commit();
        sharedPreferences.edit().remove(USER_ID).commit();
        sharedPreferences.edit().remove(ACTIVE_EVENT_ID).commit();
        sharedPreferences.edit().remove(TRACKING_ENABLED).commit();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if (cachedUsername != null) {
            sharedPreferences.edit().putString(USER_NAME, cachedUsername).commit();
        }

        if (cachedUserId != null) {
            sharedPreferences.edit().putString(USER_ID, cachedUserId).commit();
        }

        if (cachedActiveEventId != null) {
            sharedPreferences.edit().putString(ACTIVE_EVENT_ID, cachedActiveEventId).commit();
        }

        sharedPreferences.edit().putBoolean(TRACKING_ENABLED, cachedTrackingEnabled).commit();
    }

    @SmallTest
    public void testStoreUserNameThrowsNullPointerExceptionIfNoUserNameProvided() {
        try {
            service.storeUserName(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testStoreUserNameThrowsIllegalArgumentExceptionIfEmptyUserNameProvided() {
        try {
            service.storeUserName("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetUsernameReturnsNullIfNoUserNamePresent() {
        // When
        String userName = service.getUserName();

        // Then
        assertNull(userName);
    }

    @SmallTest
    public void testStoreAndRetrieveUserNameCorrectly() {
        // Given
        String userName = "Joe";

        // When
        service.storeUserName(userName);

        // Then
        assertEquals(userName, service.getUserName());
    }

    @SmallTest
    public void testRemoveUserNameRemovesUserName(){
        // Given
        String userName = "Joe";
        service.storeUserName(userName);

        // When
        service.removeUserName();

        // Then
        assertNull(service.getUserName());
    }

    @SmallTest
    public void testStoreUserIdThrowsNullPointerExceptionIfNoUserIdProvided() {
        try {
            service.storeUserId(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testStoreUserIdThrowsIllegalArgumentExceptionIfEmptyUserIdProvided() {
        try {
            service.storeUserId("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetUserIdReturnsNullIfNoUserIdPresent() {
        // When
        String userName = service.getUserId();

        // Then
        assertNull(userName);
    }

    @SmallTest
    public void testStoreAndRetrieveUserIdCorrectly() {
        // Given
        String userId = randomUUID().toString();

        // When
        service.storeUserId(userId);

        // Then
        assertEquals(userId, service.getUserId());
    }

    @SmallTest
    public void testGetActiveEventIdReturnsNullIfNoEventIdPresent() {
        // When
        String eventId = service.getActiveEventId();

        // Then
        assertNull(eventId);
    }

    @SmallTest
    public void testStoreAndRetrieveActiveEventIdCorrectly() {
        // Given
        String eventId = "eventId";

        // When
        service.storeActiveEventId(eventId);

        // Then
        assertEquals(eventId, service.getActiveEventId());
    }

    @SmallTest
    public void testStoreAndRetrieveActiveEventIdNull(){
        // Given
        service.storeActiveEventId("eventId");

        // When
        service.storeActiveEventId(null);

        // Then
        assertNull(service.getActiveEventId());
    }

    @SmallTest
    public void testGetTrackingEnabledReturnsFalseIfNoFlagPresent(){
        // When
        boolean trackingEnabled = service.getTrackingEnabled();

        // Then
        assertFalse(trackingEnabled);
    }

    @SmallTest
    public void testStoreAndRetrieveVersionCodeCorrectly(){
        // Given
        int versionCode = 300;

        // When
        service.storeCurrentVersionCode(versionCode);
        Integer result = service.getCurrentVersionCode();

        // Then
        assertEquals(versionCode, result.intValue());
    }

    @SmallTest
    public void testGetCurrentVersionCodeReturnsNullIfVersionCodeNotAvailable(){
        // When
        Integer result = service.getCurrentVersionCode();

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetTrackingEnabledReturnsStoredValue(){
        // Given
        boolean value = true;
        sharedPreferences.edit().putBoolean(TRACKING_ENABLED, value).commit();

        // When
        boolean trackingEnabled = service.getTrackingEnabled();

        // Then
        assertEquals(trackingEnabled, value);
    }
}
