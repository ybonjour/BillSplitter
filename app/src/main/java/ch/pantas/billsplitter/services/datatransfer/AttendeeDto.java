package ch.pantas.billsplitter.services.datatransfer;

import java.util.UUID;

public class AttendeeDto {
    private UUID attendeeId;
    private UUID participantId;

    public UUID getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(UUID attendeeId) {
        this.attendeeId = attendeeId;
    }

    public UUID getParticipantId() {
        return participantId;
    }

    public void setParticipantId(UUID participantId) {
        this.participantId = participantId;
    }
}
