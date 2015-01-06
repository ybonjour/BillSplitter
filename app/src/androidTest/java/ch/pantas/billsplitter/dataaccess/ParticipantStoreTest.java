package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
    public void testGetParticipantsReturnsResultOfGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Participant> participants = store.getParticipants(randomUUID());

        // Then
        assertEquals(EMPTY_LIST, participants);
    }

    @SmallTest
    public void testGetParticipantsQueriesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getParticipants(eventId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(EVENT, eventId.toString()))));
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
    public void testGetParticipantsForUserReturnsResultOfGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Participant> participants = store.getParticipantsForUsers(randomUUID());

        // Then
        assertEquals(EMPTY_LIST, participants);
    }

    @SmallTest
    public void testGetParticipantForUsersQueriesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getParticipantsForUsers(eventId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(USER, eventId.toString()))));
    }

    @SmallTest
    public void testGetParticipantsForEventAndUserThrowsNullPointerExceptionIfNoEventIdProvided() {
        try {
            store.getParticipant(null, randomUUID());
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantForEventAndUserThrowsNullPointerExceptionIfNoUserIdProvided() {
        try {
            store.getParticipant(randomUUID(), null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
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
            store.getParticipant(randomUUID(), randomUUID());
            fail("No exception has been thrown");
        } catch (IllegalStateException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantForEventAndUserQueriesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        UUID userId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getParticipant(eventId, userId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(2),
                hasEntry(EVENT, eventId.toString()), hasEntry(USER, userId.toString()))));
    }

    @SmallTest
    public void testGetParticipantForEventAndUserReturnsParticipantIfExists() {
        // Given
        Participant participant = mock(Participant.class);
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(participant));

        // When
        Participant result = store.getParticipant(randomUUID(), randomUUID());

        // Then
        assertEquals(participant, result);
    }

    @SmallTest
    public void testGetParticipantForEventAndUserReturnsNullIfNoParticipantexists() {
        // Given
        Participant participant = mock(Participant.class);
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        Participant result = store.getParticipant(randomUUID(), randomUUID());

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testRemoveAllThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.removeAll((UUID) null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllUsesCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();

        // When
        store.removeAll(eventId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(1), hasEntry(EVENT, eventId.toString()))));
    }

    @SmallTest
    public void testRemoveByThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.removeBy(null, randomUUID());
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            store.removeBy(randomUUID(), null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByUsesCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        UUID userId = randomUUID();

        // When
        store.removeBy(eventId, userId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(2), hasEntry(EVENT, eventId.toString()), hasEntry(USER, userId.toString()))));
    }


}
