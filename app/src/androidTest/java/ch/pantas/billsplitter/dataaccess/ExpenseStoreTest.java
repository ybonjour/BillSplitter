package ch.pantas.billsplitter.dataaccess;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.rowmapper.ExpenseRowMapper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Expense;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.EVENT;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.ExpenseTable.OWNER;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpenseStoreTest extends BaseMockitoInstrumentationTest {

    private static final List<Expense> EMPTY_LIST = new LinkedList<Expense>();

    @Mock
    private ExpenseRowMapper mapper;

    @Mock
    private GenericStore<Expense> genericStore;

    @Inject
    private ExpenseStore store;

    @Override
    protected Module getDefaultModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<GenericStore<Expense>>() {
                }).toInstance(genericStore);
            }
        };
    }

    @SmallTest
    public void testGetExpensesOfEventThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.getExpensesOfEvent(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetExpensesOfEventReturnsResultFromGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Expense> expenses = store.getExpensesOfEvent(randomUUID());

        // Then
        assertEquals(EMPTY_LIST, expenses);
    }

    @SmallTest
    public void testGetExpensesQueriesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Expense> expenses = store.getExpensesOfEvent(eventId);

        // Then
        assertEquals(EMPTY_LIST, expenses);
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(1), hasEntry(EVENT, eventId.toString()))));
    }

    @SmallTest
    public void testGetExpensesOfEventWithOwnerIdThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.getExpensesOfEvent(null, randomUUID());
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetExpensesOfEventWithOwnerIdThrowsNullPointerExceptionIfNoOwnerProvided() {
        try {
            store.getExpensesOfEvent(randomUUID(), null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testGetExpensesOfEventWithOwnerIdReturnsResultFromGenericStore() {
        // Given
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Expense> expenses = store.getExpensesOfEvent(randomUUID(), randomUUID());

        // Then
        assertEquals(EMPTY_LIST, expenses);
    }

    @SmallTest
    public void testGetExpensesOfEventWithOwnerIdQueriesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        UUID ownerId = randomUUID();
        when(genericStore.getModelsByQuery(anyMap())).thenReturn(EMPTY_LIST);

        // When
        List<Expense> expenses = store.getExpensesOfEvent(eventId, ownerId);

        // Then
        assertEquals(EMPTY_LIST, expenses);
        verify(genericStore, times(1)).getModelsByQuery(argThat(allOf(hasSize(2), hasEntry(EVENT, eventId.toString()), hasEntry(OWNER, ownerId.toString()))));
    }

    @SmallTest
    public void testRemoveAllThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.removeAll((UUID) null);
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllRemovesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();

        // When
        store.removeAll(eventId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(1), hasEntry(EVENT, eventId.toString()))));
    }

    @SmallTest
    public void testRemoveAllWithOwnerThrowsNullPointerExceptionIfNoEventProvided() {
        try {
            store.removeAll(null, randomUUID());
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllWithOwnerThrowsNullPointerExceptionIfNoOwnerProvided() {
        try {
            store.removeAll(randomUUID(), null);
            fail("No exception has been thrown.");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveAllWithOwnerRemovesWithCorrectWhereArguments() {
        // Given
        UUID eventId = randomUUID();
        UUID ownerId = randomUUID();

        // When
        store.removeAll(eventId, ownerId);

        // Then
        verify(genericStore, times(1)).removeAll(argThat(allOf(hasSize(2), hasEntry(EVENT, eventId.toString()), hasEntry(OWNER, ownerId.toString()))));
    }
}
