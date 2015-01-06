package ch.pantas.billsplitter.services.datatransfer;


import android.test.suitebuilder.annotation.SmallTest;

import java.util.List;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;

import static java.util.UUID.randomUUID;

public class EventDtoOperatorTest extends BaseMockitoInstrumentationTest {
    private User user;
    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        user = new User(randomUUID(), "Joe");
        event = new Event(randomUUID(), "An event", SupportedCurrency.CHF, user.getId());
    }

    @SmallTest
    public void testConstructorThrowsNullPointerExceptionIfNoDtoProvided() {
        try {
            new EventDtoOperator(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testHaParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));
        try {
            operator.hasParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testHasParticipantReturnsFalseIfParticipantIsNotSet() {
        // Given
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));

        // When
        boolean result = operator.hasParticipant(user);

        // Then
        assertFalse(result);
    }

    @SmallTest
    public void testHasParticipantReturnsTrueIfParticipantIsSet() {
        // Given
        EventDto dto = createEventDtoWithParticipant(user);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        boolean result = operator.hasParticipant(user);

        // Then
        assertTrue(result);
    }

    @SmallTest
    public void testReplaceUserThrowsNullPointerExceptionIfNoUserProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));
        try {
            operator.replaceUser(null, user);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testReplaceUserThrowsNullPointerExceptionIfNoReplacementProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));
        try {
            operator.replaceUser(user, null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testReplaceUserReplacesUserIfItExists() {
        // Given
        User replacement = new User(randomUUID(), "Replacement");
        EventDto dto = createEventDtoWithParticipant(user);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        operator.replaceUser(user, replacement);

        // Then
        assertEquals(operator.getParticipants().size(), 1);
        assertEquals(replacement, operator.getParticipants().get(0).getUser());
    }

    @SmallTest
    public void testReplaceUserDoesNotReplaceUserIfItDoesExists() {
        // Given
        User other = new User(randomUUID(), "Another user");
        User replacement = new User(randomUUID(), "Replacement");
        EventDto dto = createEventDtoWithParticipant(other);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        operator.replaceUser(user, replacement);

        // Then
        assertEquals(operator.getParticipants().size(), 1);
        assertEquals(other, operator.getParticipants().get(0).getUser());
    }

    @SmallTest
    public void testGetParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));
        try {
            operator.getParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetParticipantReturnsCorrectUser() {
        // Given
        EventDto dto = createEventDtoWithParticipant(user);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        ParticipantDto participantDto = operator.getParticipant(user);

        // Then
        assertNotNull(participantDto);
        assertEquals(user, participantDto.getUser());
    }

    @SmallTest
    public void testGetParticipantReturnsNullIfUserIsNotParticipant() {
        // Given
        User other = new User(randomUUID(), "Another user");
        EventDto dto = createEventDtoWithParticipant(other);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        ParticipantDto participantDto = operator.getParticipant(user);

        // Then
        assertNull(participantDto);
    }

    @SmallTest
    public void testGetExpensesOfOwnerThrowsNullPointerExceptionIfNoOwnerUserIdIsProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));
        try {
            operator.getExpensesOfOwner(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetExpensesOfOwnerReturnsExpenseIfItIsOfOwner() {
        // Given
        EventDto dto = createEventDtoWithExpensePayedBy(user);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        List<ExpenseDto> expenseDtos = operator.getExpensesOfOwner(user.getId());

        // Then
        assertEquals(1, expenseDtos.size());
        assertEquals(user.getId(), expenseDtos.get(0).getExpense().getOwnerId());
    }

    @SmallTest
    public void testGetExpensesOfOwnerDoesNotReturnExpenseIfItIsOfOtherOwner() {
        // Given
        User other = new User(randomUUID(), "Another user");
        EventDto dto = createEventDtoWithExpensePayedBy(user);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        List<ExpenseDto> expenseDtos = operator.getExpensesOfOwner(other.getId());

        // Then
        assertEquals(0, expenseDtos.size());
    }

    @SmallTest
    public void testConfirmParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));

        try {
            operator.confirmParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    // TODO: if this behavior is really correct
    @SmallTest
    public void testConfirmParticipantAddsParticipantAndConfirmsParticipantIfItDoesNotExist() {
        // Given
        EventDto dto = createEventDtoWithEvent(event);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        operator.confirmParticipant(user);

        // Then
        assertEquals(1, operator.getParticipants().size());
        assertEquals(user, operator.getParticipants().get(0).getUser());
        assertTrue(operator.getParticipants().get(0).isConfirmed());
    }

    @SmallTest
    public void testConfirmParticipantConfirmsParticipant() {
        // Given
        EventDto dto = createEventDtoWithParticipant(user);
        EventDtoOperator operator = new EventDtoOperator(dto);

        // When
        operator.confirmParticipant(user);

        // Then
        assertEquals(1, operator.getParticipants().size());
        assertEquals(user, operator.getParticipants().get(0).getUser());
        assertTrue(operator.getParticipants().get(0).isConfirmed());
    }

    @SmallTest
    public void testAddParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        EventDtoOperator operator = new EventDtoOperator(createEventDtoWithEvent(event));

        try {
            operator.addParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testAddParticipantAddsNewParticipant() {
        // Given
        EventDto eventDto = createEventDtoWithEvent(event);
        EventDtoOperator operator = new EventDtoOperator(eventDto);

        // When
        ParticipantDto participantDto = operator.addParticipant(user);

        // Then
        assertEquals(1, operator.getParticipants().size());
        assertEquals(participantDto, operator.getParticipants().get(0));
    }

    @SmallTest
    public void testAddParticipantCreatesCorrectParticipant() {
        // Given
        EventDto eventDto = createEventDtoWithEvent(event);
        EventDtoOperator operator = new EventDtoOperator(eventDto);

        // When
        ParticipantDto participantDto = operator.addParticipant(user);

        // Then
        assertEquals(user, participantDto.getUser());
        assertNotNull(participantDto.getParticipantId());
        assertFalse(participantDto.isConfirmed());
        assertEquals(0, participantDto.getLastUpdated());
    }

    @SmallTest
    public void testGetUnconfirmedUserDoesNotReturnConfirmedUser() {
        // Given
        EventDto eventDto = createEventDtoWithParticipant(user);
        eventDto.getParticipants().get(0).setConfirmed(true);
        EventDtoOperator operator = new EventDtoOperator(eventDto);

        // When
        List<ParticipantDto> participantDtos = operator.getUnconfirmedParticipants();

        // Then
        assertEquals(0, participantDtos.size());
    }

    @SmallTest
    public void testGetUnconfirmedUserReturnsUnconfirmedUser() {
        // Given
        EventDto eventDto = createEventDtoWithParticipant(user);
        eventDto.getParticipants().get(0).setConfirmed(false);
        EventDtoOperator operator = new EventDtoOperator(eventDto);

        // When
        List<ParticipantDto> participantDtos = operator.getUnconfirmedParticipants();

        // Then
        assertEquals(1, participantDtos.size());
        assertEquals(user, participantDtos.get(0).getUser());
    }

    private EventDto createEventDtoWithExpensePayedBy(User user) {
        EventDto eventDto = createEventDtoWithParticipant(user);
        Expense expense = new Expense(randomUUID(), event.getId(), user.getId(), "An expense", 2200, user.getId());
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setExpense(expense);
        eventDto.addExpense(expenseDto);

        return eventDto;
    }


    private EventDto createEventDtoWithEvent(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setEvent(event);

        return eventDto;
    }

    private EventDto createEventDtoWithParticipant(User user) {
        EventDto eventDto = createEventDtoWithEvent(event);

        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setUser(user);
        eventDto.addParticipant(participantDto);

        return eventDto;
    }
}
