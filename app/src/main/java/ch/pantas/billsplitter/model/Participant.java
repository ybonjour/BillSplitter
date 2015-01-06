package ch.pantas.billsplitter.model;

import java.util.UUID;

public class Participant extends Model {
    private UUID userId;
    private UUID eventId;
    private boolean confirmed;
    private long lastUpdated;

    public Participant(UUID id, UUID userId, UUID eventId, boolean confirmed, long lastUpdated) {
        super(id);
        this.userId = userId;
        this.eventId = eventId;
        this.confirmed = confirmed;
        this.lastUpdated = lastUpdated;
    }

    public Participant(UUID userId, UUID eventId) {
        this(userId, eventId, false, 0);
    }

    public Participant(UUID userId, UUID eventId, boolean confirmed, long lastUpdated) {
        this.userId = userId;
        this.eventId = eventId;
        this.confirmed = confirmed;
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        if (!eventId.equals(that.eventId)) return false;
        if (!userId.equals(that.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + eventId.hashCode();
        return result;
    }
}
