package ch.pantas.billsplitter.model;

public class Participant extends Model {
    private String userId;
    private String eventId;
    private boolean confirmed;

    public Participant(String id, String userId, String eventId, boolean confirmed) {
        super(id);
        this.userId = userId;
        this.eventId = eventId;
        this.confirmed = confirmed;
    }

    public Participant(String userId, String eventId){
        this(userId, eventId, false);
    }

    public Participant(String userId, String eventId, boolean confirmed){
        this.userId = userId;
        this.eventId = eventId;
        this.confirmed = confirmed;
    }

    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

    public boolean isConfirmed() { return confirmed; }

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
}
