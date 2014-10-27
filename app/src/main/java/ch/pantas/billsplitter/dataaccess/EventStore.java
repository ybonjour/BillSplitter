package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import ch.pantas.billsplitter.dataaccess.rowmapper.EventRowMapper;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Event;

import static com.google.inject.Key.get;
import static roboguice.RoboGuice.getInjector;

@Singleton
public class EventStore extends BaseStore<Event> {

    @Inject
    public EventStore(EventRowMapper mapper, GenericStore<Event> genericStore) {
        super(mapper, genericStore);
    }
}
