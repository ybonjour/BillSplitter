package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.AttendeeStore;
import ch.pantas.billsplitter.dataaccess.ExpenseStore;
import ch.pantas.billsplitter.dataaccess.UserStore;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;

public class DebtCalculatorTest extends BaseMockitoInstrumentationTest {

    @Inject
    private DebtCalculator debtCalculator;

    @Mock
    private ExpenseStore expenseStore;

    @Mock
    private UserStore userStore;

    @Mock
    private AttendeeStore attendeeStore;

    @SmallTest
    public void testCalculateDebtsThrowsNullPointerExceptionIfNoEventIsProvided() {
        try {
            debtCalculator.calculateDebts(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    // TODO: further tests should be added after optimization algorithm is finished
}
