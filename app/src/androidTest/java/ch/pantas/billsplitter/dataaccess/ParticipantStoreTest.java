package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.rowmapper.ParticipantRowMapper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ParticipantTable.USER;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParticipantStoreTest extends BaseMockitoInstrumentationTest {

    private static final List<Participant> EMPTY_LIST = new LinkedList<Participant>();

    @Mock
    private ParticipantRowMapper mapper;

    @Mock
    private UserStore userStore;

    @Mock
    private GenericStore<Participant> genericStore;

    @Inject
    private ParticipantStore store;


    @Override
    protected Module getDefaultModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<GenericStore<Participant>>() {
                }).toInstance(genericStore);
            }
        };
    }

    @SmallTest
    public void testGetParticipantsThrowsNullPointerExceptionIfNoEventIdProvided() {
        try {
            store.getParticipants(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsThrowsIllegalArgumentExceptionIfEmptyEventIdProvided() {
        try {
            store.getParticipants("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsReturnsResultOfGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Participant> participants = store.getParticipants("someEventId");

        // Then
        assertEquals(EMPTY_LIST, participants);
    }

    @SmallTest
    public void testGetParticipantsQueriesWithCorrectWhereArguments() {
        // Given
        String eventId = randomUUID().toString();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getParticipants(eventId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(EVENT, eventId))));
    }

    @SmallTest
    public void testGetParticipantsForUserThrowsNullPointerExceptionIfNoUserIdProvided() {
        try {
            store.getParticipantsForUsers(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsForUserThrowsIllegalArgumentExceptionIfEmptyUserIdProvided() {
        try {
            store.getParticipants("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantsForUserReturnsResultOfGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Participant> participants = store.getParticipantsForUsers("someUserid");

        // Then
        assertEquals(EMPTY_LIST, participants);
    }

    @SmallTest
    public void testGetParticipantForUsersQueriesWithCorrectWhereArguments() {
        // Given
        String eventId = randomUUID().toString();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getParticipantsForUsers(eventId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(USER, eventId))));
    }

    @SmallTest
    public void testGetParticipantsForEventAndUserThrowsNullPointerExceptionIfNoEventIdProvided() {
        try {
            store.getParticipant(null, randomUUID().toString());
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantForEventAndUserThrowsNullPointerExceptionIfNoUserIdProvided() {
        try {
            store.getParticipant(randomUUID().toString(), null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantForEventAndUserThrowsIllegalArgumentExceptionIfEmptyEventProvided() {
        try {
            store.getParticipant("", randomUUID().toString());
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantForEventAndUserThrowsIllegalArgumentExceptionIfEmptyUserProvided() {
        try {
            store.getParticipant(randomUUID().toString(), "");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantThrowsIllegalStateExceptionIfMoreThanOneUserExists() {
        // Given
        Participant participant1 = mock(Participant.class);
        Participant participant2 = mock(Participant.class);
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(participant1, participant2));

        try {
            store.getParticipant(randomUUID().toString(), randomUUID().toString());
            fail("No exception has been thrown");
        } catch (IllegalStateException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantForEventAndUserQueriesWithCorrectWhereArguments() {
        // Given
        String eventId = randomUUID().toString();
        String userId = randomUUID().toString();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getParticipant(eventId, userId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(2),
                hasEntry(EVENT, eventId), hasEntry(USER, userId))));
    }

    @SmallTest
    public void testGetParticipantForEventAndUserReturnsParticipantIfExists() {
        // Given
        Participant participant = mock(Participant.class);
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(participant));

        // When
        Participant result = store.getParticipant("eventId", "userId");

        // Then
        assertEquals(participant, result);
    }

    @SmallTest
    public void testGetParticipantForEventAndUserReturnsNullIfNoParticipantexists() {
        // Given
        Participant participant = mock(Participant.class);
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        Participant result = store.getParticipant("eventId", "userId");

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testRemoveAllThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.removeAll((String) null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllThrowsIllegalArgumentExceptionIfEmptyEventProvided() {
        try {
            store.removeAll("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllUsesCorrectWhereArguments() {
        // Given
        String eventId = randomUUID().toString();

        // When
        store.removeAll(eventId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(1), hasEntry(EVENT, eventId))));
    }

    @SmallTest
    public void testRemoveByThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.removeBy(null, randomUUID().toString());
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            store.removeBy(randomUUID().toString(), null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByThrowsIllegalArgumentExceptionIfEmptyEventIdProvided() {
        try {
            store.removeBy("", randomUUID().toString());
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByThrowsIllegalArgumentExceptionIfEmptyUserIdProvided() {
        try {
            store.removeBy(randomUUID().toString(), "");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByUsesCorrectWhereArguments() {
        // Given
        String eventId = randomUUID().toString();
        String userId = randomUUID().toString();

        // When
        store.removeBy(eventId, userId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(2), hasEntry(EVENT, eventId), hasEntry(USER, userId))));
    }


}
