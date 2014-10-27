package ch.pantas.billsplitter.services;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.AttendeeDto;
import ch.pantas.billsplitter.services.datatransfer.EventDtoOperator;
import ch.pantas.billsplitter.services.datatransfer.ExpenseDto;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;

public class ImportService {

    @Inject
    private UserService userService;
    @Inject
    private EventStore eventStore;
    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private UserStore userStore;

    public void deepImportEvent(EventDtoOperator eventDto){

        User me = userService.getMe();
        Event event = eventDto.getEvent();

        createEventIfNotExists(event);

        if (!event.getOwnerId().equals(me.getId())) {
            eventStore.persist(event);
        }

        importParticipants(eventDto);

        for(ParticipantDto participantDto : eventDto.getParticipants()){
            Participant participant = participantStore.getById(participantDto.participantId);

            // Never touch my own expenses
            if (participant.getUserId().equals(me.getId())) continue;

            if(participant.getLastUpdated() < participantDto.lastUpdated){
                removeExpensesOfOwner(eventDto.getEvent(), participant.getUserId());
                importExpensesOfOwner(eventDto, participant.getUserId());
                participant.setLastUpdated(participantDto.lastUpdated);
                participantStore.persist(participant);
            }
        }
    }

    private void removeExpensesOfOwner(Event event, String userId){
        if(userId == null) return;

        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId(), userId);
        for(Expense expense : expenses){
            attendeeStore.removeAll(expense.getId());
        }

        expenseStore.removeAll(event.getId(), userId);
    }

    private void importExpensesOfOwner(EventDtoOperator eventDto, String ownerUserId) {
        for (ExpenseDto expenseDto : eventDto.getExpensesOfOwner(ownerUserId)) {
            Expense expense = expenseDto.expense;
            expenseStore.createExistingModel(expense);

            for (AttendeeDto attendeeDto : expenseDto.attendingParticipants) {
                Attendee attendee = new Attendee(attendeeDto.attendeeId, expense.getId(), attendeeDto.participantId);
                attendeeStore.createExistingModel(attendee);
            }
        }
    }

    private User importParticipant(ParticipantDto participantDto, Event event) {
        User newUser = participantDto.user;
        createUserIfNotExists(newUser);

        Participant participant = participantStore.getById(participantDto.participantId);
        if(participant == null){
            createParticipant(participantDto.participantId, newUser, event);
        } else {
            User oldUser = userStore.getById(participant.getUserId());
            if(!oldUser.equals(newUser)) {
                removeIfPossible(oldUser);
            }

            updateParticipant(participant, participantDto);
            participantStore.persist(participant);
        }

        return newUser;
    }

    private void updateParticipant(Participant participant, ParticipantDto participantDto){
        participant.setConfirmed(participantDto.confirmed);
        participant.setUserId(participantDto.user.getId());
    }

    private void createParticipant(String participantId, User user, Event event){
        Participant newParticipant = new Participant(participantId, user.getId(), event.getId(), true, 0);
        participantStore.createExistingModel(newParticipant);
    }

    private void createUserIfNotExists(User user){
        User existingUser = userStore.getById(user.getId());
        if (existingUser == null) {
            String username = userService.findBestFreeNameForUser(user);
            user.setName(username);
            userStore.createExistingModel(user);
        }
    }

    private void removeIfPossible(User user) {
        if (!participatesInMultipleEvents(user)) {
            userStore.removeById(user.getId());
        }
    }

    private boolean participatesInMultipleEvents(User user) {
        return participantStore.getParticipantsForUsers(user.getId()).size() > 1;
    }

    private void createEventIfNotExists(Event event){
        Event existingEvent = eventStore.getById(event.getId());
        if(existingEvent == null) {
            eventStore.createExistingModel(event);
        }
    }

    private void importParticipants(EventDtoOperator eventDto){
        for (ParticipantDto participantDto : eventDto.getParticipants()) {
            importParticipant(participantDto, eventDto.getEvent());
        }
    }
}
