package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.View;

import com.google.android.apps.common.testing.ui.espresso.UiController;
import com.google.android.apps.common.testing.ui.espresso.ViewAction;

import org.hamcrest.Matcher;
import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.framework.BaseEspressoTest;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Expense;
import ch.pantas.billsplitter.model.SupportedCurrency;
import ch.pantas.billsplitter.model.User;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.billsplitter.services.DebtCalculator;
import ch.pantas.billsplitter.services.EventService;
import ch.pantas.billsplitter.services.SharedPreferenceService;
import ch.pantas.billsplitter.services.UserService;
import ch.pantas.billsplitter.ui.actions.ShareAction;
import ch.pantas.billsplitter.ui.adapter.EventDetailPagerAdapter;
import ch.pantas.billsplitter.ui.fragment.BaseEventDetailsFragment;
import ch.pantas.splitty.R;

import static ch.pantas.billsplitter.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isAssignableFrom;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventDetailsTest extends BaseEspressoTest<EventDetails> {
    public EventDetailsTest() {
        super(EventDetails.class);
    }

    @Mock
    private EventStore eventStore;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private ActivityStarter activityStarter;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Mock
    private UserService userService;

    //@Mock
    //private ActionProvider actionProvider;

    @Mock
    private DebtCalculator debtCalculator;

    @Mock
    private EventDetailTabs eventDetailTabs;

    @Mock
    private BaseEventDetailsFragment mockFragment;

    @Mock
    private ViewPager viewPager;

    @Mock
    private EventDetailPagerAdapter pagerAdapter;

    @Mock
    private EventService eventService;

    @Mock
    private ShareAction shareAction;

    private User user;
    private Event event;
    private Expense expense;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Given
        user = new User("userId", "userName");
        event = new Event("eventId", "EventName", SupportedCurrency.CHF, user.getId());
        expense = new Expense("expenseId", event.getId(), event.getOwnerId(), "expense description", 1000, event.getOwnerId());

        when(eventStore.getById(event.getId())).thenReturn(event);
        when(expenseStore.getById(expense.getId())).thenReturn(expense);
        when(expenseStore.getExpensesOfEvent(event.getId())).thenReturn(asList(expense));

        when(sharedPreferenceService.getUserId()).thenReturn(user.getId());

        when(userService.getMe()).thenReturn(user);

        when(eventDetailTabs.init(event)).thenReturn(eventDetailTabs);
        when(eventDetailTabs.getFragment(anyInt(), any(EventDetailPagerAdapter.class))).thenReturn(mockFragment);

        when(viewPager.getAdapter()).thenReturn(pagerAdapter);
        when(pagerAdapter.init(eventDetailTabs)).thenReturn(pagerAdapter);
        when(pagerAdapter.getCount()).thenReturn(3);
        when(pagerAdapter.getPageTitle(0)).thenReturn("TabA");
        when(pagerAdapter.getPageTitle(1)).thenReturn("TabB");
        when(pagerAdapter.getPageTitle(2)).thenReturn("TabC");


        Intent intent = new Intent();
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        setActivityIntent(intent);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Tabs
    @SmallTest
    public void testTabsAreShown() {
        // When
        getActivity();

        // Then
        onView(withText("TabA")).check(matches(isDisplayed()));
        onView(withText("TabB")).check(matches(isDisplayed()));
        onView(withText("TabC")).check(matches(isDisplayed()));
    }

    // Menu Actions
    @SmallTest
    public void testMenuAddExpense() {
        // When
        getActivity();
        onView(withId(R.id.action_add_expense)).perform(click());

        // Then
        verify(activityStarter, times(1)).startAddExpense(any(Context.class), eq(event));
    }

    @SmallTest
    public void testMenuDeleteEvent() {
        // When
        getActivity();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_delete_event)).perform(click());
        onView(withText(R.string.delete)).perform(click());

        // Then
        verify(eventService, times(1)).removeEventAndGetActiveEvent(eq(event));
    }

    @SmallTest
    public void testMenuDeleteEventCancel() {
        // When
        getActivity();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_delete_event)).perform(click());
        onView(withText(R.string.cancel)).perform(click());

        // Then
        verify(eventService, times(0)).removeEventAndGetActiveEvent(eq(event));
    }


    @SmallTest
    public void testMenuEditEvent() {
        // When
        getActivity();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_edit_event)).perform(click());

        // Then
        verify(activityStarter, times(1)).startEditEvent(any(Context.class), eq(event));
    }

    @SmallTest
    @Suppress
    public void testMenuShare() {
        // When
        getActivity();
        onView(withId(R.id.action_share)).perform(click());

        // Then
        // TODO: execute is called on mock but test still fails
        verify(shareAction, times(1)).execute(any(EventDetails.class));
    }

    @SmallTest
    public void testMenuSettings() {
        // When
        getActivity();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.settings)).perform(click());

        // Then
        verify(activityStarter, times(1)).startSettings(any(Context.class));
    }

    @SmallTest
    public void testMenuBeam() {
        // When
        getActivity();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.beam_event)).perform(click());

        // Then
        verify(activityStarter, times(1)).startBeamEvent(any(Context.class), eq(event));
    }

    // Navigation Drawer
    @SmallTest
    public void testOpenDrawerShowsNameAndEvents() {
        // When
        getActivity();
        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer());

        // Then
        onView(withText(user.getName())).check(matches(isDisplayed()));
        onView(withText(event.getName())).check(matches(isDisplayed()));
    }

    @SmallTest
    public void testOpenDrawerAddEvent() {
        // When
        getActivity();
        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(withId(R.id.add_group_button)).perform(click());

        // Then
        verify(activityStarter, times(1)).startAddEvent(any(Context.class));
    }

    @SmallTest
    @Suppress
    public void testOpenDrawerEditName() {
        // TODO: test is failing
        // When
        getActivity();
        onView(withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(withText(user.getName())).perform(click());
        onView(withId(R.id.hidden_edit_view)).perform(clearText(), typeText("newname"));
        onView(withId(R.id.drawer_layout)).perform(actionCloseDrawer(), actionOpenDrawer());

        // Then
        verify(userService, times(1)).changeMyUsername(eq("newname"));
    }

    private static void waitABit() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static ViewAction actionOpenDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).openDrawer(GravityCompat.START);
                waitABit();
            }
        };
    }

    private static ViewAction actionCloseDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "close drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).closeDrawer(GravityCompat.START);
            }
        };
    }
}

