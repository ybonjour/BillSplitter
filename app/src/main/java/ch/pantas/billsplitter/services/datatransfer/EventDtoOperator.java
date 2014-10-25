package ch.pantas.billsplitter.services.datatransfer;


import java.util.List;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.EventDto;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;

import static java.util.UUID.randomUUID;

public class EventDtoOperator {

    private final EventDto eventDto;

    public EventDtoOperator(EventDto eventDto){
        this.eventDto = eventDto;
    }

    public boolean isParticipant(User user){
        return getParticipant(user) != null;
    }

    public void replaceUser(User user, User replace) {
        for (ParticipantDto participant : eventDto.participants) {
            if (participant.user.equals(user)) {
                participant.user = replace;
            }
        }
    }

    public ParticipantDto getParticipant(User user){
        for(ParticipantDto participantDto : eventDto.participants){
            if(participantDto.user.equals(user)){
                return participantDto;
            }
        }

        return null;
    }

    public Event getEvent(){
        return eventDto.event;
    }

    public String getSenderUserId(){
        if(eventDto.expenses.isEmpty()) return null;

        return eventDto.expenses.get(0).expense.getOwnerId();
    }

    public List<ParticipantDto> getParticipants(){
        return eventDto.participants;
    }

    public List<ExpenseDto> getExpenses() {
        return eventDto.expenses;
    }

    public void addParticipant(User user){
        ParticipantDto dtoNew = new ParticipantDto();
        dtoNew.confirmed = true;
        dtoNew.participantId = randomUUID().toString();
        dtoNew.user = user;

        eventDto.participants.add(dtoNew);
    }
}
