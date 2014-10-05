package ch.pantas.billsplitter.model;

public class Participant extends Model {
    private String userId;
    private String eventId;

    public Participant(String id, String userId, String eventId) {
        super(id);
        this.userId = userId;
        this.eventId = eventId;
    }

    public Participant(String userId, String eventId){
        this.userId = userId;
        this.eventId = eventId;
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
}
