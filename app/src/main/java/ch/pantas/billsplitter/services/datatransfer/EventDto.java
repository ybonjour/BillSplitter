package ch.pantas.billsplitter.services.datatransfer;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.Event;

public class EventDto {
    private Event event;

    private List<ParticipantDto> participants = new LinkedList<ParticipantDto>();
    private List<ExpenseDto> expenses = new LinkedList<ExpenseDto>();

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<ParticipantDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }

    public List<ExpenseDto> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseDto> expenses) {
        this.expenses = expenses;
    }

    public void addParticipant(ParticipantDto participant) {
        participants.add(participant);
    }

    public void addExpense(ExpenseDto expense){
        expenses.add(expense);
    }
}
