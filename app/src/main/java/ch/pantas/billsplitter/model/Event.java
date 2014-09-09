package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Event extends Model {
    private final String name;

    public Event(String id, String name) {
        super(id);
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
