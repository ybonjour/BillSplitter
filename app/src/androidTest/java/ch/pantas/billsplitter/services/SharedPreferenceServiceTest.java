package ch.pantas.billsplitter.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;

import static ch.pantas.billsplitter.services.SharedPreferenceService.USER_NAME;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SharedPreferenceServiceTest extends BaseMockitoInstrumentationTest {

    @Inject
    private SharedPreferenceService service;

    @Inject
    private SharedPreferences sharedPreferences;

    private String cachedUsername;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        cachedUsername = sharedPreferences.getString(USER_NAME, null);
        sharedPreferences.edit().remove(USER_NAME).commit();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if(cachedUsername != null){
            sharedPreferences.edit().putString(USER_NAME, cachedUsername).commit();
        }
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
    public void testStoreAndRetrieveUserNameCorrectly(){
        // Given
        String userName = "Joe";

        // When
        service.storeUserName(userName);

        // Then
        assertEquals(userName, service.getUserName());
    }

    @SmallTest
    public void testGetActiveEventIdReturnsNullIfNoEventIdPresent() {
        // When
        String eventId = service.getActiveEventId();

        // Then
        assertNull(eventId);
    }

    @SmallTest
    public void testStoreAndRetrieveActiveEventIdCorrectly(){
        // Given
        String eventId = "eventId";

        // When
        service.storeActiveEventId(eventId);

        // Then
        assertEquals(eventId, service.getActiveEventId());
    }

}
