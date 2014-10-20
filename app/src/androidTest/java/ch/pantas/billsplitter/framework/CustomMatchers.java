package ch.pantas.billsplitter.framework;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.User;

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
}
