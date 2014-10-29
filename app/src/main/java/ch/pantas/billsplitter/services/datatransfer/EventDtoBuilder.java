package ch.pantas.billsplitter.services.datatransfer;

import com.google.gson.Gson;

import java.util.List;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class EventDtoBuilder {

    EventDto eventDto;

    public EventDtoBuilder() {
        init();
    }

    private void init() {
        eventDto = new EventDto();
    }

    public EventDto build() {
        return eventDto;
    }

    public void withEvent(Event event) {
        checkNotNull(event);
        eventDto.setEvent(event);
    }

    public void withParticipants(List<ParticipantDto> participants) {
        checkNotNull(participants);
        eventDto.setParticipants(participants);
    }

    public void withExpenses(List<ExpenseDto> expenseDtos) {
        checkNotNull(expenseDtos);

        eventDto.setExpenses(expenseDtos);
    }

    static public String convertToJson(EventDto eventDto) {
        return new Gson().toJson(eventDto);
    }

    static public EventDto createFromJson(String json) {
        Gson gson = new Gson();
        EventDto eventDto = gson.fromJson(json, EventDto.class);

        return eventDto;
    }

}
