package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Expense extends Model {
    private final String eventId;
    private final String payerId;
    private final String description;
    private final double amount;

    public Expense(String id, String eventId, String payerId, String description, double amount) {
        super(id);
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(payerId);
        checkArgument(!payerId.isEmpty());
        checkNotNull(description);
        checkArgument(amount > 0.0);

        this.eventId = eventId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
    }

    public Expense(String eventId, String payerId, String description, double amount) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(payerId);
        checkArgument(!payerId.isEmpty());
        checkNotNull(description);
        checkArgument(amount > 0.0);

        this.eventId = eventId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return getAmount() + " for " + getDescription();
    }

    public String getEventId() { return eventId; }

    public String getPayerId() { return payerId; }

    public String getDescription() { return description; }

    public double getAmount() { return amount; }
}