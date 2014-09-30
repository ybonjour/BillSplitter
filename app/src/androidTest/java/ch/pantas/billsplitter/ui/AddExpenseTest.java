package ch.pantas.billsplitter.ui;

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.EditText;

import com.google.android.apps.common.testing.ui.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.User;
import ch.yvu.myapplication.R;

import static ch.pantas.billsplitter.ui.ExpensesList.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.lang.Double.parseDouble;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddExpenseTest extends BaseEspressoTest<AddExpense> {
    public AddExpenseTest() {
        super(AddExpense.class);
    }

    @Mock
    private EventStore eventStore;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private UserStore userStore;

    private Event event;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        event = new Event("abcd", "An Event");
        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
        when(eventStore.getById(event.getId())).thenReturn(event);
    }

    @LargeTest
    public void testTitleIsEventName() {
        // When
        getActivity();

        // Then
        onView(withText(event.getName())).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testInitiallyPayerFieldIsEnabled() {
        // When
        getActivity();

        // Then
        onView(withId(R.id.expense_payer)).check(matches(isEnabled()));
    }

    @LargeTest
    public void testClickingOnMeCheckboxDisablesPayerField() {
        // Given
        getActivity();

        // When
        onView(withId(R.id.expense_payer_me)).perform(click());

        // Then
        onView(withId(R.id.expense_payer)).check(matches(not(isEnabled())));
    }

    @LargeTest
    public void testClickingOnMeCheckboxWritesMeToPayerField() {
        // Given
        getActivity();
        onView(withId(R.id.expense_payer)).perform(typeText("A name"));

        // When
        onView(withId(R.id.expense_payer_me)).perform(click());

        // Then
        onView(withId(R.id.expense_payer)).check(matches(editTextWithText(R.string.me)));
    }

    @LargeTest
    public void testUncheckingOnMeCheckboxEnablesPayerField(){
        // Given
        getActivity();
        onView(withId(R.id.expense_payer_me)).perform(click());

        // When
        onView(withId(R.id.expense_payer_me)).perform(click());

        // Then
        onView(withId(R.id.expense_payer)).check(matches(isEnabled()));
    }

    @LargeTest
    public void testUncheckingOnMeCheckboxEmptiesPayerField(){
        // Given
        getActivity();
        onView(withId(R.id.expense_payer)).perform(typeText("A name"));
        onView(withId(R.id.expense_payer_me)).perform(click());

        // When
        onView(withId(R.id.expense_payer_me)).perform(click());

        // Then
        onView(withId(R.id.expense_payer)).check(matches(emptyEditText()));
    }

    @LargeTest
    public void testSaveButtonIsDisplayed(){
        // When
        getActivity();

        // Then
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    @LargeTest
    public void testExpenseIsAddedIfSaveButtonIsPressedWithNewUser(){
        // Given
        String description = "An expense";
        String amount = "25.00";
        final String payerId = "abc";
        String payer = "Joe";
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));
        onView(withId(R.id.expense_amount)).perform(typeText(amount));
        onView(withId(R.id.expense_payer)).perform(typeText(payer));

        when(userStore.getUserWithName(payer)).thenReturn(null);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                user.setId(payerId);

                return null;
            }
        }).when(userStore).persist(any(User.class));


        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(userStore, times(1)).persist(argThat(userWithName(payer)));
        verify(expenseStore, times(1)).persist(argThat(newExpenseWith(description, parseDouble(amount), event.getId(), payerId)));
    }

    @LargeTest
    public void testExpenseIsAddedIfSaveButtonIsPressedWithExistingUser(){
        // Given
        String description = "An expense";
        String amount = "25.00";
        String payer = "Joe";
        String payerId = "abc";
        getActivity();
        onView(withId(R.id.expense_description)).perform(typeText(description));
        onView(withId(R.id.expense_amount)).perform(typeText(amount));
        onView(withId(R.id.expense_payer)).perform(typeText(payer));
        User user = new User(payerId, payer);
        when(userStore.getUserWithName(payer)).thenReturn(user);

        // When
        onView(withText(R.string.save)).perform(click());

        // Then
        verify(userStore, times(1)).persist(eq(user));
        verify(expenseStore, times(1)).persist(argThat(newExpenseWith(description, parseDouble(amount), event.getId(), payerId)));
    }

    private static Matcher<User> userWithName(final String name) {
        return new TypeSafeMatcher<User>() {
            @Override
            public boolean matchesSafely(User user) {
                return name.equals(user.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("User with name ");
                description.appendText(name);
            }
        };
    }

    private static Matcher<Expense> newExpenseWith(final String expenseDescription, final double amount, final String eventId, final String userId){
        return new TypeSafeMatcher<Expense>() {
            @Override
            public boolean matchesSafely(Expense expense) {
                if(!expense.isNew()) return false;
                if(!expenseDescription.equals(expense.getDescription())) return false;
                if(amount != expense.getAmount()) return false;
                if(!eventId.equals(expense.getEventId())) return false;
                if(userId != null && !userId.equals(expense.getPayerId())) return false;

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("New expense with description ");
                description.appendText(expenseDescription);
                description.appendText(" and amount ");
                description.appendValue(amount);
                description.appendText(" and event ");
                description.appendText(eventId);
            }
        };
    }

    private static Matcher<View> editTextWithText(final int resId) {
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

    private static Matcher<View> emptyEditText(){
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
