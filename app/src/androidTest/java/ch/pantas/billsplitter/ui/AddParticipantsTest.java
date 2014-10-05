package ch.pantas.billsplitter.ui;

import android.test.suitebuilder.annotation.LargeTest;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.yvu.myapplication.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class AddParticipantsTest extends BaseEspressoTest<AddParticipants> {

    public AddParticipantsTest() {
        super(AddParticipants.class);
    }

    @Mock
    private EventStore eventStore;

    @LargeTest
    public void testCorrectTitleIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.add_event)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testSaveButtonIsDisplayed() {
        // When
        getActivity();

        // Then
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    // TODO: Participants UI tests
}
