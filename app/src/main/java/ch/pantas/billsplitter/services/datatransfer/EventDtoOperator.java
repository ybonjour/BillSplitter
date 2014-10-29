package ch.pantas.billsplitter.services.datatransfer;


import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.User;

import static java.util.UUID.randomUUID;

public class EventDtoOperator {

    private final EventDto eventDto;

    public EventDtoOperator(EventDto eventDto) {
        this.eventDto = eventDto;
    }

    public boolean hasParticipant(User user) {
        return getParticipant(user) != null;
    }

    public void replaceUser(User user, User replace) {
        for (ParticipantDto participant : eventDto.getParticipants()) {
            if (participant.getUser().equals(user)) {
                participant.setUser(replace);
            }
        }
    }

    public ParticipantDto getParticipant(User user) {
        for (ParticipantDto participantDto : eventDto.getParticipants()) {
            if (participantDto.getUser().equals(user)) {
                return participantDto;
            }
        }

        return null;
    }

    public Event getEvent() {
        return eventDto.getEvent();
    }

    public List<ParticipantDto> getParticipants() {
        return eventDto.getParticipants();
    }

    public List<ExpenseDto> getExpensesOfOwner(String ownerUserId) {
        List<ExpenseDto> expenses = new LinkedList<ExpenseDto>();
        for (ExpenseDto expenseDto : eventDto.getExpenses()) {
            if (expenseDto.getExpense().getOwnerId().equals(ownerUserId)) {
                expenses.add(expenseDto);
            }
        }

        return expenses;
    }

    public void confirmParticipant(User user){
        ParticipantDto dto = getParticipant(user);
        if(dto == null){
            dto = addParticipant(user);
        }
        dto.setConfirmed(true);
    }

    public ParticipantDto addParticipant(User user) {
        ParticipantDto dtoNew = new ParticipantDto();
        dtoNew.setParticipantId(randomUUID().toString());
        dtoNew.setUser(user);

        eventDto.addParticipant(dtoNew);

        return dtoNew;
    }

    public List<ParticipantDto> getUnconfirmedParticipants() {
        List<ParticipantDto> unconfirmedParticipants = new LinkedList<ParticipantDto>();
        for (ParticipantDto participantDto : eventDto.getParticipants()) {
            if (!participantDto.isConfirmed()) {
                unconfirmedParticipants.add(participantDto);
            }
        }
        return unconfirmedParticipants;
    }
}
