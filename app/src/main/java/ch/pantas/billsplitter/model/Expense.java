package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Expense extends Model {
    private final String eventId;

    private String payerId;
    private String description;
    private int amount;

    public Expense(String id, String eventId, String payerId, String description, int amount) {
        super(id);
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(payerId);
        checkArgument(!payerId.isEmpty());
        checkArgument(amount > 0);

        this.eventId = eventId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
    }

    public Expense(String eventId, String payerId, String description, int amount) {
        checkNotNull(eventId);
        checkArgument(!eventId.isEmpty());
        checkNotNull(payerId);
        checkArgument(!payerId.isEmpty());
        checkArgument(amount > 0);

        this.eventId = eventId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
    }

    public String getEventId() { return eventId; }

    public String getPayerId() { return payerId; }

    public String getDescription() { return description; }

    public int getAmount() { return amount; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
}