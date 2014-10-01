package ch.pantas.billsplitter.framework;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

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

}
