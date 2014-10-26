package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Event extends Model {
    private String name;
    private SupportedCurrency currency;
    private String ownerId;

    public Event(String id, String name, SupportedCurrency currency, String ownerId) {
        super(id);
        checkNotNull(name);
        checkArgument(!name.isEmpty());
        checkNotNull(ownerId);
        checkArgument(!ownerId.isEmpty());

        this.name = name;
        this.currency = currency;
        this.ownerId = ownerId;
    }

    public Event(String name, SupportedCurrency currency, String ownerId) {
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        this.name = name;
        this.currency = currency;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupportedCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(SupportedCurrency currency) {
        this.currency = currency;
    }

    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
