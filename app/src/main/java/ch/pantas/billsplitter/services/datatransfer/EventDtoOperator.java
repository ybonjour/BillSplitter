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
        for (ParticipantDto participant : eventDto.participants) {
            if (participant.user.equals(user)) {
                participant.user = replace;
            }
        }
    }

    public ParticipantDto getParticipant(User user) {
        for (ParticipantDto participantDto : eventDto.participants) {
            if (participantDto.user.equals(user)) {
                return participantDto;
            }
        }

        return null;
    }

    public Event getEvent() {
        return eventDto.event;
    }

    public List<ParticipantDto> getParticipants() {
        return eventDto.participants;
    }

    public List<ExpenseDto> getExpensesOfOwner(String ownerUserId) {
        List<ExpenseDto> expenses = new LinkedList<ExpenseDto>();
        for (ExpenseDto expenseDto : eventDto.expenses) {
            if (expenseDto.expense.getOwnerId().equals(ownerUserId)) {
                expenses.add(expenseDto);
            }
        }

        return expenses;
    }

    public void addParticipant(User user) {
        ParticipantDto dtoNew = new ParticipantDto();
        dtoNew.confirmed = true;
        dtoNew.participantId = randomUUID().toString();
        dtoNew.user = user;

        eventDto.participants.add(dtoNew);
    }

    public List<ParticipantDto> getUnconfirmedParticipants() {
        List<ParticipantDto> unconfirmedParticipants = new LinkedList<ParticipantDto>();
        for (ParticipantDto participantDto : eventDto.participants) {
            if (!participantDto.confirmed) {
                unconfirmedParticipants.add(participantDto);
            }
        }
        return unconfirmedParticipants;
    }
}
