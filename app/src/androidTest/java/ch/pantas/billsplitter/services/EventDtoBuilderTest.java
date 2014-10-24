package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.EventDtoBuilder;

import static org.mockito.Mockito.when;

public class EventDtoBuilderTest  extends BaseMockitoInstrumentationTest {

    @Inject
    private EventDtoBuilder eventDtoBuilder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @SmallTest
    public void testEventDtoBuilderFromEvent() {
        // Given
        SupportedCurrency currency = SupportedCurrency.valueOf("CHF");
        Event event = new Event("testEventId", "testEventName", currency);

        User payer = new User("payerId", "Payer");

        List<Expense> expensesOfEvent = new LinkedList<Expense>();
        expensesOfEvent.add(new Expense("expenseId1", event.getId(), payer.getId(), "desc1", 111));
        expensesOfEvent.add(new Expense("expenseId2", event.getId(), payer.getId(), "desc2", 222));

        List<User> participantsList = new LinkedList<User>();
        participantsList.add(new User("userId1", "user1"));
        participantsList.add(new User("userId2", "user2"));
        participantsList.add(new User("userId3", "user3"));

        List<User> attendeeList = new LinkedList<User>();
        attendeeList.add(participantsList.get(0));
        attendeeList.add(participantsList.get(1));

        List<User> attendeeList2 = new LinkedList<User>();
        attendeeList2.add(participantsList.get(1));
        attendeeList2.add(participantsList.get(2));

        // When
        eventDtoBuilder.setEvent(event);
        eventDtoBuilder.setParticipants(participantsList);
        eventDtoBuilder.addExpense(expensesOfEvent.get(0), attendeeList);
        eventDtoBuilder.addExpense(expensesOfEvent.get(1), attendeeList2);
        EventDto eventDto = eventDtoBuilder.create();

        // Then
        String json = EventDtoBuilder.convertToJson(eventDto);
        EventDto result = EventDtoBuilder.createFromJson(json);
        assertEquals(result.event, event);
    }
}
