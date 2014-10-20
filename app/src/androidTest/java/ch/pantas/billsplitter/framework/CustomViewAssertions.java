package ch.pantas.billsplitter.framework;

import com.google.android.apps.common.testing.ui.espresso.ViewAssertion;

import static ch.pantas.billsplitter.framework.CustomViewMatchers.editTextWithText;
import static ch.pantas.billsplitter.framework.CustomViewMatchers.withBackgroundColor;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;

public class CustomViewAssertions {

    public static ViewAssertion hasBackgroundColor(int resId) {
        return matches(withBackgroundColor(resId));
    }

    public static ViewAssertion hasText(String text) {
        return matches(editTextWithText(text));
    }
}
