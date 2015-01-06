package ch.pantas.billsplitter.model;

import java.util.UUID;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Expense extends Model {
    private final UUID eventId;

    private UUID payerId;
    private String description;
    private int amount;
    private UUID ownerId;

    public Expense(UUID id, UUID eventId, UUID payerId, String description, int amount, UUID ownerId) {
        super(id);
        checkNotNull(eventId);
        checkNotNull(payerId);
        checkArgument(amount > 0);
        checkNotNull(ownerId);

        this.eventId = eventId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
        this.ownerId = ownerId;
    }

    public Expense(UUID eventId, UUID payerId, String description, int amount, UUID ownerId) {
        checkNotNull(eventId);
        checkNotNull(payerId);
        checkArgument(amount > 0);
        checkNotNull(ownerId);

        this.eventId = eventId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
        this.ownerId = ownerId;
    }

    public UUID getEventId() { return eventId; }

    public UUID getPayerId() { return payerId; }

    public String getDescription() { return description; }

    public int getAmount() { return amount; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPayerId(UUID payerId) {
        this.payerId = payerId;
    }

    public UUID getOwnerId() { return ownerId; }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}