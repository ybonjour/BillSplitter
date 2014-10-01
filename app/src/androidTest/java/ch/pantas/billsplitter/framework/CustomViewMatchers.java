package ch.pantas.billsplitter.framework;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;

import com.google.android.apps.common.testing.ui.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CustomViewMatchers {

    public static Matcher<View> withBackgroundColor(final int resId){
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {

                Drawable backgroundDrawable = view.getBackground();
                if(!(backgroundDrawable instanceof ColorDrawable)) return false;

                int backgroundColor = ((ColorDrawable) backgroundDrawable).getColor();

                int expectedColor = view.getResources().getColor(resId);

                return backgroundColor == expectedColor;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("View with background color ");
                description.appendValue(resId);
            }
        };
    }

    public static Matcher<View> editTextWithText(final int resId) {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            protected boolean matchesSafely(EditText editText) {
                String text = editText.getResources().getString(resId);
                if (text == null) return false;

                return text.equals(editText.getText().toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has content text with resource id ");
                description.appendText(String.valueOf(resId));
            }
        };
    }

    public static Matcher<View> emptyEditText() {
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            protected boolean matchesSafely(EditText editText) {
                return "".equals(editText.getText().toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("empty EditText");
            }
        };
    }
}
