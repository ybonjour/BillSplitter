package ch.pantas.billsplitter.ui;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ParticipantManager {

    private List<User> fixedParticipants = new LinkedList<User>();
    private List<User> participants = new LinkedList<User>();

    public void addParticipant(User user) {
        checkNotNull(user);

        if (isAlreadyParticipant(user)) return;

        participants.add(user);
    }

    public void addFixedParticipant(User user) {
        checkNotNull(user);

        if (isFixedParticipant(user)) return;

        participants.remove(user);
        fixedParticipants.add(user);
    }

    public List<User> getParticipants() {
        List<User> allParticipants = new LinkedList<User>();

        for (User user : fixedParticipants) {
            allParticipants.add(user);
        }

        for (User user : participants) {
            allParticipants.add(user);
        }

        return allParticipants;
    }

    public List<User> filterOutParticipants(List<User> input) {
        List<User> nonParticipants = new LinkedList<User>();

        for (User user : input) {
            if (!isAlreadyParticipant(user)) {
                nonParticipants.add(user);
            }
        }

        return nonParticipants;
    }

    public void removeParticipant(User user) {
        checkNotNull(user);

        participants.remove(user);

    }

    public boolean isFixedParticipant(User user) {
        return fixedParticipants.contains(user);
    }

    private boolean isAlreadyParticipant(User user) {
        if (isFixedParticipant(user)) return true;

        return participants.contains(user);
    }

    public void clear() {
        participants.clear();
        fixedParticipants.clear();
    }
}
