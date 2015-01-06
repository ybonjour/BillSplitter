package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.List;

import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.AttendeeDto;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.ExpenseDto;

import static ch.pantas.billsplitter.framework.CustomMatchers.matchesAttendee;
import static ch.pantas.billsplitter.framework.CustomMatchers.matchesParticipant;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ImportServiceTest extends ImportServiceBaseTest {

    @Mock
    private UserService userService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(userService.getMe()).thenReturn(me);
    }

    @SmallTest
    public void testImportEventThrowsNullPointerExceptionIfNoEventDtoProvided() {
        try {
            importService.importEvent(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testImportEventCreatesEventIfItDoesNotYetExist() {
        // Given
        when(eventStore.getById(event.getId())).thenReturn(null);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(eventStore, times(1)).createExistingModel(event);
    }

    @SmallTest
    public void testImportEventDoesNotCreateEventIfItDoesAlreadyExist() {
        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(eventStore, never()).createExistingModel(event);
    }

    @SmallTest
    public void testImportEventOverridesEventIfMeIsNotTheEventOwner() {
        // Given
        eventDto.getEvent().setOwnerId(randomUUID());

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(eventStore, times(1)).persist(event);
    }

    @SmallTest
    public void testImportDoesNotOverrideEventChangesIfMeIsTheEventOwner() {
        // Given
        eventDto.getEvent().setOwnerId(me.getId());

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(eventStore, never()).persist(event);
    }

    @SmallTest
    public void testImportEventCreatesUserWithCorrectNameIfItDoesNotExist() {
        // Given
        Participant participant = addParticipant(eventDto, user);
        when(participantStore.getById(participant.getId()))
                .thenReturn(null)
                .thenReturn(participant);
        when(userStore.getById(user.getId())).thenReturn(null);
        String newUsername = "A new username";
        when(userService.findBestFreeNameForUser(user)).thenReturn(newUsername);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(userStore, times(1)).createExistingModel(user);
        assertEquals(newUsername, user.getName());
    }

    @SmallTest
    public void testImportEventDoesNotCreateNewUserIfItAlreadyExists() {
        // Given
        Participant participant = addParticipant(eventDto, user);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(userStore, never()).createExistingModel(user);
    }

    @SmallTest
    public void testImportEventCreatesParticipantIfItDoesNotExist() {
        // Given
        Participant participant = addParticipant(eventDto, user, true, currentTimeMillis());
        when(participantStore.getById(participant.getId()))
                .thenReturn(null)
                .thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        ArgumentCaptor<Participant> participantArgumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(participantStore, times(1)).createExistingModel(participantArgumentCaptor.capture());
        Participant persistedParticipant = participantArgumentCaptor.getValue();
        assertEquals(participant.getId(), persistedParticipant.getId());
        assertThat(persistedParticipant, matchesParticipant(participant.getUserId(),
                participant.getEventId(), participant.isConfirmed(), 0));
    }

    @SmallTest
    public void testImportEventUpdatesExistingParticipantIfItExists() {
        // Given
        boolean confirmed = false;
        long lastUpdated = currentTimeMillis();
        Participant participant = addParticipant(eventDto, user, confirmed, 0);
        participant.setConfirmed(true); // Shall be overridden from dto
        participant.setLastUpdated(lastUpdated); // Shall not be overridden
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(participantStore, times(1)).persist(argThat(matchesParticipant(user.getId(), event.getId(), confirmed, lastUpdated)));
    }

    @SmallTest
    public void testImportEventReplacesUserOfExistingParticipant() {
        // Given
        Participant participant = addParticipant(eventDto, user, false, currentTimeMillis());

        User existingUser = new User(randomUUID(), "Mary");
        when(userStore.getById(existingUser.getId())).thenReturn(existingUser);
        participant.setUserId(existingUser.getId());
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(participantStore, times(1)).persist(argThat(matchesParticipant(user.getId(), event.getId(), participant.isConfirmed(), participant.getLastUpdated())));
    }

    @SmallTest
    public void testImportEventRemovesOldUserAfterReplacingIt() {
        // Given
        Participant participant = addParticipant(eventDto, user, false, currentTimeMillis());

        User existingUser = new User(randomUUID(), "Mary");
        when(userStore.getById(existingUser.getId())).thenReturn(existingUser);
        participant.setUserId(existingUser.getId());
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        when(participantStore.getParticipantsForUsers(existingUser.getId())).thenReturn(asList(participant));

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(userStore, times(1)).removeById(existingUser.getId());
    }

    @SmallTest
    public void testImportEventDoesNotRemoveOldUserAfterReplacingItIfHeParticipatesInOtherEvents() {
        // Given
        Participant participant = addParticipant(eventDto, user, false, currentTimeMillis());

        User existingUser = new User(randomUUID(), "Mary");
        when(userStore.getById(existingUser.getId())).thenReturn(existingUser);
        participant.setUserId(existingUser.getId());
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        Participant participantOtherEvent = new Participant(randomUUID(), user.getId(), randomUUID(), false, 0);
        when(participantStore.getParticipantsForUsers(existingUser.getId())).thenReturn(asList(participant, participantOtherEvent));

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(userStore, never()).removeById(existingUser.getId());
    }

    @SmallTest
    public void testImportEventUpdatesLastUpdatedOfParticipant() {
        // Given
        long lastUpdated = currentTimeMillis();
        Participant participant = addParticipant(eventDto, user, false, lastUpdated);
        participant.setLastUpdated(0);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        InOrder inOrder = inOrder(participantStore);
        inOrder.verify(participantStore).persist(any(Participant.class)); // changes to user and confirmed flag
        inOrder.verify(participantStore).persist(argThat(matchesParticipant(user.getId(), event.getId(), participant.isConfirmed(), lastUpdated)));
    }

    @SmallTest
    public void testImportEventDoesNotUpdateLastUpdatedOfParticipantIfParticipantIsMe() {
        // Given
        long lastUpdated = currentTimeMillis();
        Participant participant = addParticipant(eventDto, me, false, lastUpdated);
        participant.setLastUpdated(0);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(participantStore, times(1)).persist(any(Participant.class));
        verify(participantStore, never()).persist(argThat(matchesParticipant(user.getId(), event.getId(), participant.isConfirmed(), lastUpdated)));
    }

    @SmallTest
    public void testImportEventDoesNotUpdateLastUpdatedOfParticipantIfDataInPersistenceIsNewer() {
        // Given
        long lastUpdated = currentTimeMillis();
        Participant participant = addParticipant(eventDto, me, false, lastUpdated - 1);
        participant.setLastUpdated(lastUpdated);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(participantStore, times(1)).persist(any(Participant.class));
        verify(participantStore, never()).persist(argThat(matchesParticipant(user.getId(), event.getId(), participant.isConfirmed(), lastUpdated)));
    }

    @SmallTest
    public void testImportEventRemovesAndAddsExpensesOfUser() {
        // Given
        Participant participant = addParticipant(eventDto, user, false, currentTimeMillis());
        participant.setLastUpdated(0);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        Expense expense = addExpense(eventDto, user);
        when(expenseStore.getExpensesOfEvent(event.getId(), user.getId())).thenReturn(asList(expense));

        addAttendee(eventDto.getExpenses().get(0), participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(attendeeStore, times(1)).removeAll(expense.getId());
        verify(expenseStore, times(1)).removeAll(event.getId(), user.getId());
        verify(expenseStore, times(1)).createExistingModel(expense);
        verify(attendeeStore, times(1)).createExistingModel(argThat(matchesAttendee(expense.getId(), participant.getId())));
    }

    @SmallTest
    public void testImportEventDoesNeitherRemoveNorAddExpensesOfUserIfItIsMe() {
        // Given
        Participant participant = addParticipant(eventDto, me, false, currentTimeMillis());
        participant.setLastUpdated(0);
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        Expense expense = addExpense(eventDto, me);
        when(expenseStore.getExpensesOfEvent(event.getId(), me.getId())).thenReturn(asList(expense));
        addAttendee(eventDto.getExpenses().get(0), participant);

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verifyZeroInteractions(attendeeStore, expenseStore);
    }

    private Expense addExpense(EventDto eventDto, User payer) {
        ExpenseDto expenseDto = new ExpenseDto();
        Expense expense = new Expense(randomUUID(), event.getId(), payer.getId(), "An expense", 200, payer.getId());
        expenseDto.setExpense(expense);

        eventDto.addExpense(expenseDto);

        return expense;
    }

    private Attendee addAttendee(ExpenseDto expenseDto, Participant attendingParticipant) {
        Attendee attendee = new Attendee(randomUUID(), expenseDto.getExpense().getId(), attendingParticipant.getId());

        AttendeeDto attendeeDto = new AttendeeDto();
        attendeeDto.setParticipantId(attendingParticipant.getId());
        attendeeDto.setAttendeeId(attendee.getId());

        List<AttendeeDto> attendeeDtos = expenseDto.getAttendingParticipants();
        attendeeDtos.add(attendeeDto);
        expenseDto.setAttendingParticipants(attendeeDtos);

        return attendee;
    }

}