package ch.pantas.billsplitter.services;

import com.google.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.ParticipantStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;

public class JsonBuilder {

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private UserStore userStore;

    public JSONObject fromEvent(Event event) throws JSONException {
        JSONObject eventJson = new JSONObject();
        eventJson.put("event_id", event.getId());
        eventJson.put("event_name", event.getName());
        eventJson.put("event_currency", event.getCurrency());

        List<Expense> expensesOfEvent = expenseStore.getExpensesOfEvent(event.getId());
        JSONArray expensesListJson = new JSONArray();
        for (Expense expense : expensesOfEvent) {
            expensesListJson.put(fromExpense(expense));
        }
        eventJson.put("expenses_list", expensesListJson);

        List<User> participants = participantStore.getParticipants(event.getId());
        JSONArray participantsListJson = new JSONArray();
        for (User participant : participants) {
            participantsListJson.put(fromUser(participant));
        }
        eventJson.put("participants_list", participantsListJson);

        return eventJson;
    }

    public JSONObject fromExpense(Expense expense) throws JSONException {
        JSONObject expenseJson = new JSONObject();
        expenseJson.put("expense_id", expense.getId());
        expenseJson.put("event_id", expense.getEventId());
        expenseJson.put("expense_description", expense.getDescription());
        expenseJson.put("expense_payer_id", expense.getPayerId());
        expenseJson.put("expense_amount", expense.getAmount());

        List<User> attendees = attendeeStore.getAttendees(expense.getId());
        JSONArray attendeeListJson = new JSONArray();
        for (User attendee : attendees) {
            attendeeListJson.put(fromUser(attendee));
        }
        expenseJson.put("attendee_list", attendeeListJson);

        return expenseJson;
    }

    public JSONObject fromUser(User user) throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("user_id", user.getId());
        userJson.put("user_name", user.getName());

        return userJson;
    }
}
