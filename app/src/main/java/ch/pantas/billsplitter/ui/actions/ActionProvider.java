package ch.pantas.billsplitter.ui.actions;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ActionProvider {
    private Map<Integer, EventDetailsAction> eventDetailsActions = new HashMap<Integer, EventDetailsAction>();


    public void addEventDetailsAction(int id, EventDetailsAction action) {
        checkNotNull(action);
        eventDetailsActions.put(id, action);
    }

    public EventDetailsAction getEventDetailsAction(int id) {
        return eventDetailsActions.get(id);
    }

}
