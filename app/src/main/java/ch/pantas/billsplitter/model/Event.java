package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Event extends Model {
    private String name;
    private Currency currency;

    public Event(String id, String name, Currency currency) {
        super(id);
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        this.name = name;
        this.currency = currency;
    }

    public Event(String name, Currency currency) {
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        this.name = name;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if(!(o instanceof Event)) return false;

        Event event = (Event) o;

        if(getId() == null && event.getId() == null){
            return getName().equals(event.getName()) && getCurrency().equals(event.getCurrency());
        } else {
            return getId().equals(event.getId());
        }
    }
}
