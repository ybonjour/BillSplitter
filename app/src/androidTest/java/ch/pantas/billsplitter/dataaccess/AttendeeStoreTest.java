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

import ch.pantas.billsplitter.dataaccess.rowmapper.AttendeeRowMapper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Participant;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.AttendeeTable.EXPENSE;
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

public class AttendeeStoreTest extends BaseMockitoInstrumentationTest {

    private static final List<Attendee> EMPTY_LIST = new LinkedList<Attendee>();

    @Mock
    private GenericStore<Attendee> genericStore;

    @Mock
    private Attendee attendee;

    @Mock
    private AttendeeRowMapper attendeeRowMapper;

    @Mock
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore store;


    @Override
    protected Module getDefaultModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<GenericStore<Attendee>>() {
                }).toInstance(genericStore);
            }
        };
    }

    @SmallTest
    public void testGetAttendeesThrowsNullPointerExceptionIfNoExpenseIdProvided() {
        try {
            store.getAttendees(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetAttendeesReturnsResultOfGenericStore() {
        // Given
        UUID expenseId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Attendee> attendees = store.getAttendees(expenseId);

        // Then
        assertEquals(EMPTY_LIST, attendees);
    }

    @SmallTest
    public void testGetAttendeesUsesCorrectWhereParameters() {
        // Given
        UUID expenseId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        store.getAttendees(expenseId);

        // Then
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(EXPENSE, expenseId.toString()))));
    }

    @SmallTest
    public void testGetAttendingParticipantsThrowsNullPointerExceptionIfNoExpenseIdProvided() {
        try {
            store.getAttendingParticipants(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetAttendingParticipantsReturnsEmptyListIfNoAttendeesExist() {
        // Given
        UUID expenseId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Participant> participants = store.getAttendingParticipants(expenseId);

        // Then
        assertNotNull(participants);
        assertEquals(0, participants.size());
    }

    @SmallTest
    public void testGetAttendingParticipantsReturnsCorrectParticipant() {
        // Given
        UUID expenseId = randomUUID();
        Attendee attendee = new Attendee(randomUUID(), randomUUID(), randomUUID());
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(attendee));

        Participant participant = new Participant(attendee.getParticipant(), randomUUID(), randomUUID(), false, 0);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        List<Participant> participants = store.getAttendingParticipants(expenseId);

        // Then
        assertNotNull(participants);
        assertEquals(1, participants.size());
        assertEquals(participant, participants.get(0));
    }

    @SmallTest
    public void testGetAttendingParticipantsDoesNotReturnParticipantIfItDoesNotExist() {
        // Given
        UUID expenseId = randomUUID();
        Attendee attendee = new Attendee(randomUUID(), randomUUID(), randomUUID());
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(asList(attendee));

        when(participantStore.getById(attendee.getParticipant())).thenReturn(null);

        // When
        List<Participant> participants = store.getAttendingParticipants(expenseId);

        // Then
        assertNotNull(participants);
        assertEquals(0, participants.size());
        assertEquals(0, participants.size());
    }

    @SmallTest
    public void testRemoveAllThrowsNullPointerExceptionIfNoExpenseIdProvided() {
        try {
            store.removeAll((UUID) null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllCallsRemoveAllWithCorrectWhereArgument() {
        // Given
        UUID expenseId = randomUUID();

        // When
        store.removeAll(expenseId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(1), hasEntry(EXPENSE, expenseId.toString()))));
    }
}