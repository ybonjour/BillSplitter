package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Event;

@Singleton
public class EventStore extends BaseStore<Event> {

    @Inject
    public EventStore(EventRowMapper mapper) {
        super(mapper);
    }
}
