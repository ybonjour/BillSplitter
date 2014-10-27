package ch.pantas.billsplitter.model;

public class Participant extends Model {
    private String userId;
    private String eventId;
    private boolean confirmed;
    private long lastUpdated;

    public Participant(String id, String userId, String eventId, boolean confirmed, long lastUpdated) {
        super(id);
        this.userId = userId;
        this.eventId = eventId;
        this.confirmed = confirmed;
        this.lastUpdated = lastUpdated;
    }

    public Participant(String userId, String eventId) {
        this(userId, eventId, false, 0);
    }

    public Participant(String userId, String eventId, boolean confirmed, long lastUpdated) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
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
