package ch.pantas.billsplitter.services.datatransfer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventDtoBuilder {
    EventDto eventDto;

    public EventDtoBuilder() {
        init();
    }

    public EventDtoBuilder(Event event, List<User> participants) {
        init();
        setEvent(event);
        setParticipants(participants);
    }

    private void init() {
        eventDto = new EventDto();
        eventDto.expenses = new LinkedList<ExpenseDto>();
        //eventDto.attendees = new HashMap<Expense, List<User>>();

    }

    public EventDto create() {
        return eventDto;
    }

    public void setEvent(Event event) {
        checkNotNull(event);
        eventDto.event = event;
    }

    public void setParticipants(List<User> participants) {
        checkNotNull(participants);
        eventDto.participants = participants;
    }

    public void addExpense(Expense expense, List<User> attendees) {
        checkNotNull(expense);
        checkNotNull(attendees);
        //eventDto.expenses.add(expense);
        //eventDto.attendees.put(expense, attendees);

        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.expense = expense;
        expenseDto.attendees = attendees;
        eventDto.expenses.add(expenseDto);
    }

    static public String convertToJson(EventDto eventDto) {
        return new Gson().toJson(eventDto);
    }

    static public EventDto createFromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();

        Gson gson = new Gson();
        EventDto eventDto = gson.fromJson(json, EventDto.class);

        return eventDto;
    }

}
