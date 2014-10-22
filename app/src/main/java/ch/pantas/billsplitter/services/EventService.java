package ch.pantas.billsplitter.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class EventService {

    @Inject
    private EventStore eventStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    public Event removeEventAndGetActiveEvent(Event event) {
        checkNotNull(event);
        checkArgument(!event.isNew());

        eventStore.removeById(event.getId());

        String activeEventId = sharedPreferenceService.getActiveEventId();
        if(activeEventId == null) return null;

        if(activeEventId.equals(event.getId())) {
            Event newActiveEvent = getNextActiveEvent();
            String newActiveEventId = newActiveEvent != null ? newActiveEvent.getId() : null;
            sharedPreferenceService.storeActiveEventId(newActiveEventId);
            return newActiveEvent;
        } else {
            return eventStore.getById(activeEventId);
        }
    }

    private Event getNextActiveEvent(){
        List<Event> events = eventStore.getAll();

        if(events.size() == 0) return null;

        return events.get(0);
    }
}
