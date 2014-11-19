package ch.pantas.billsplitter.framework;

import android.content.ContentValues;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.Map;

import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.datatransfer.ParticipantDto;

import static com.google.inject.internal.util.$Preconditions.checkArgument;

public class CustomMatchers {

    public static Matcher<Map<String, String>> hasSize(final int size) {
        return new TypeSafeMatcher<Map<String, String>>() {
            @Override
            public boolean matchesSafely(Map<String, String> kvMap) {
                return kvMap.size() == size;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("map with size ");
                description.appendValue(size);
            }
        };
    }

    public static Matcher<User> matchesUser(final String name) {
        return new TypeSafeMatcher<User>() {
            @Override
            public boolean matchesSafely(User user) {
                if (user == null) return false;
                return user.getName().equals(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("User with name ");
                description.appendText(name);
            }
        };
    }

    public static Matcher<Debt> matchesDebt(final User from, final User to, final double amount) {
        return new TypeSafeMatcher<Debt>() {
            @Override
            public boolean matchesSafely(Debt debt) {
                if (debt == null) return false;

                return debt.getFrom().equals(from) &&
                        debt.getTo().equals(to) &&
                        debt.getAmount() == amount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Debt from ");
                description.appendText(from.getName());
                description.appendText(" to ");
                description.appendText(to.getName());
                description.appendText(" with amount");
                description.appendValue(amount);
            }
        };
    }

    public static Matcher<ContentValues> matchesContentValues(final String... values) {
        checkArgument(values.length % 2 == 0, "You must provide a value for each key.");
        return new TypeSafeMatcher<ContentValues>() {
            @Override
            public boolean matchesSafely(ContentValues contentValues) {
                if (contentValues == null) return false;

                Map<String, String> values = getValues();
                if (values.size() != contentValues.size()) return false;

                for (String key : values.keySet()) {
                    if (!contentValues.containsKey(key)) return false;

                    if (!contentValues.get(key).equals(values.get(key))) return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Content value with entries ");
                boolean first = true;
                Map<String, String> values = getValues();
                for (String key : values.keySet()) {
                    if (!first) description.appendText(",");

                    description.appendText("(");
                    description.appendText(key);
                    description.appendText(",");
                    description.appendText(values.get(key));
                    description.appendText(")");

                    first = false;
                }
            }

            private Map<String, String> getValues() {
                Map<String, String> valueMap = new HashMap<String, String>();
                for (int i = 0; i < values.length; i += 2) {
                    valueMap.put(values[i], values[i + 1]);
                }
                return valueMap;
            }
        };
    }

    public static Matcher<ParticipantDto> matchesParticipantDto(final String participantId, final Matcher<User> user, final boolean confirmed, final long lastUpdated) {
        return new TypeSafeMatcher<ParticipantDto>() {
            @Override
            public boolean matchesSafely(ParticipantDto participantDto) {
                if (participantDto == null) return false;

                return participantDto.getParticipantId().equals(participantId) &&
                        user.matches(participantDto.getUser()) &&
                        participantDto.isConfirmed() == confirmed &&
                        participantDto.getLastUpdated() == lastUpdated;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ParticipantDto with ");
                description.appendText("participantId ").appendText(participantId).appendText(",");
                description.appendText("User ");
                user.describeTo(description);
                description.appendText(", ");
                description.appendText("confirmed ").appendValue(confirmed).appendText(", ");
                description.appendText("and last updated ").appendValue(lastUpdated);

            }
        };
    }

    public static Matcher<Participant> matchesParticipant(final String userId, final String eventId, final boolean confirmed, final long lastUpdated) {
        return new TypeSafeMatcher<Participant>() {
            @Override
            public boolean matchesSafely(Participant participant) {
                if (participant == null) return false;

                return participant.getUserId().equals(userId) &&
                        participant.getEventId().equals(eventId) &&
                        participant.isConfirmed() == confirmed &&
                        participant.getLastUpdated() == lastUpdated;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Participant with ");
                description.appendText("User ").appendText(userId);
                description.appendText(", Event ").appendText(eventId);
                description.appendText(", Confirmed ").appendValue(confirmed);
                description.appendText(", Last Updated ").appendValue(lastUpdated);
            }
        };
    }

    public static Matcher<Attendee> matchesAttendee(final String expenseId, final String participantId){
        return new TypeSafeMatcher<Attendee>() {
            @Override
            public boolean matchesSafely(Attendee attendee) {
                if(attendee == null) return false;

                return attendee.getExpense().equals(expenseId) &&
                        attendee.getParticipant().equals(participantId);
            }

            @Override
            public void describeTo(Description description) {
                description
                        .appendText("Attendee with expense Id ")
                        .appendText(expenseId)
                        .appendText(" and participant Id ").appendText(participantId);
            }
        };
    }
}
