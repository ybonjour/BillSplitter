package ch.pantas.billsplitter.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.RowMapper;
import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.Model;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static ch.pantas.billsplitter.framework.CustomMatchers.hasSize;
import static ch.pantas.billsplitter.framework.CustomMatchers.matchesContentValues;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenericStoreTest extends BaseMockitoInstrumentationTest {

    private static final String DUMMY_TABLE_NAME = "dummy";

    @Mock
    private BillSplitterDatabaseOpenHelper databaseHelper;

    @Mock
    protected BillSplitterDatabase database;

    @Mock
    protected Cursor cursor;

    @Mock
    private RowMapper<DummyModel> mapper;

    @Inject
    private GenericStore<DummyModel> store;

    private DummyModel model;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(databaseHelper.getDatabase()).thenReturn(database);
        when(database.query(anyString(), anyMap())).thenReturn(cursor);
        when(database.queryWithLike(anyString(), anyMap())).thenReturn(cursor);

        store.setRowMapper(mapper);
        model = new DummyModel(randomUUID().toString());
        when(mapper.map(cursor)).thenReturn(model);
        when(mapper.getTableName()).thenReturn(DUMMY_TABLE_NAME);

        when(mapper.getValues(any(DummyModel.class))).thenAnswer(new Answer<ContentValues>() {
            @Override
            public ContentValues answer(InvocationOnMock invocation) throws Throwable {
                DummyModel model = (DummyModel) invocation.getArguments()[0];

                ContentValues values = new ContentValues();
                values.put(ID, model.getId());

                return values;
            }
        });
    }

    @SmallTest
    public void testGetAllReturnsEmptyListWithZeroModels() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        List<DummyModel> models = store.getAll();

        // Then
        assertEquals(0, models.size());
    }

    @SmallTest
    public void testGetAllReturnsCorrectModelWithOneModel() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);

        // When
        List<DummyModel> models = store.getAll();

        // Then
        assertEquals(1, models.size());
        assertEquals(model, models.get(0));
    }

    @SmallTest
    public void testGetAllQueriesDatabaseWithoutWhereArguments() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        store.getAll();

        // Then
        verify(database, times(1)).query(DUMMY_TABLE_NAME, null);
    }

    @SmallTest
    public void testGetByIdReturnsNullIfModelDoesNotExist() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        String id = "abc";

        // When
        Model result = store.getById(id);

        // Then
        assertNull(result);
    }

    @SmallTest
    public void testGetByIdReturnsCorrectModelIfItExists() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        String id = "abc";

        // When
        Model result = store.getById(id);

        // Then
        assertNotNull(result);
        assertEquals(model, result);
    }

    @SmallTest
    public void testPersistThrowsNullPointerExceptionIfNoModelProvided() {
        try {
            store.persist(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testPersistWithNewModel() {
        // Given
        DummyModel model = new DummyModel();

        // When
        store.persist(model);

        // Then
        assertNotNull(model.getId());
        verify(database, times(1)).insert(anyString(), any(ContentValues.class));
    }

    @SmallTest
    public void testPersistWithExistingModel() {
        // Given
        String id = "abc";
        DummyModel model = new DummyModel(id);

        // When
        store.persist(model);

        // Then
        assertEquals(id, model.getId());
        verify(database, times(1)).update(anyString(), argThat(matchesContentValues(ID, id)));
    }

    @SmallTest
    public void testCreateExistingModelThrowsNullPointerExceptionIfNoModelProvided() {
        try {
            store.createExistingModel(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCreateExistingModelThrowsIllegalArgumentExceptionIfNewModelProvided() {
        try {
            DummyModel model = new DummyModel();
            store.createExistingModel(model);
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testCreateExistingModelInsertsModelWithValuesFromRowMapper() {
        // Given
        DummyModel model = new DummyModel(randomUUID().toString());

        // When
        store.createExistingModel(model);

        // Then
        verify(database, times(1)).insert(eq(DUMMY_TABLE_NAME),
                argThat(matchesContentValues(ID, model.getId())));

    }

    @SmallTest
    public void testRemoveByIdThrowsNullPointerExceptionIfNoIdProvided() {
        try {
            store.removeById(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByIdThrowsIllegalArgumentExceptionIfEmptyIdProvided() {
        try {
            store.removeById("");
            fail("No exception has been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveByIdCallsRemoveAllWithCorrectWhereArgument() {
        // Given
        DummyModel model = new DummyModel(randomUUID().toString());

        // When
        store.removeById(model.getId());

        // Then
        verify(database, times(1)).removeAll(eq(DUMMY_TABLE_NAME), argThat(allOf(hasSize(1), hasEntry(ID, model.getId()))));
    }

    @SmallTest
    public void testRemoveAllAcceptsNullAsWhereArgument() {
        // When
        store.removeAll(null);

        // Then
        verify(database, times(1)).removeAll(DUMMY_TABLE_NAME, null);
    }

    @SmallTest
    public void testRemoveAllPassesOnNonNullWhereArgument() {
        // When
        HashMap<String, String> where = new HashMap<String, String>();
        store.removeAll(where);

        // Then
        verify(database, times(1)).removeAll(DUMMY_TABLE_NAME, where);
    }

    @SmallTest
    public void testGetModelsByQueryAcceptsNullAsWhereArgument() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        store.getModelsByQuery(null);

        // Then
        verify(database, times(1)).query(DUMMY_TABLE_NAME, null);
    }

    @SmallTest
    public void testGetModelsByQueryPassesOnCorrectWhereArgument() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        Map<String, String> where = new HashMap<String, String>();

        // When
        store.getModelsByQuery(where);

        // Then
        verify(database, times(1)).query(DUMMY_TABLE_NAME, where);
    }

    @SmallTest
    public void testGetModelsByQueryReturnsEmptyListIfNoModelIsStored() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        Map<String, String> where = new HashMap<String, String>();

        // When
        List<DummyModel> models = store.getModelsByQuery(where);

        // Then
        assertNotNull(models);
        assertEquals(0, models.size());
    }

    @SmallTest
    public void testGetModelsByQueryReturnsCorrectModel() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(model);
        Map<String, String> where = new HashMap<String, String>();

        // When
        List<DummyModel> models = store.getModelsByQuery(where);

        // Then
        assertNotNull(models);
        assertEquals(1, models.size());
        assertEquals(model, models.get(0));
    }

    @SmallTest
    public void testGetModelsByQueryWithLikeAcceptsNullAsWhereArgument() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);

        // When
        store.getModelsByQueryWithLike(null);

        // Then
        verify(database, times(1)).queryWithLike(DUMMY_TABLE_NAME, null);
    }

    @SmallTest
    public void testGetModelsByQueryWithLikePassesOnCorrectWhereArgument() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        Map<String, String> where = new HashMap<String, String>();

        // When
        store.getModelsByQueryWithLike(where);

        // Then
        verify(database, times(1)).queryWithLike(DUMMY_TABLE_NAME, where);
    }

    @SmallTest
    public void testGetModelsByQueryWithLikeReturnsEmptyListIfNoModelIsStored() {
        // Given
        when(cursor.moveToNext()).thenReturn(false);
        Map<String, String> where = new HashMap<String, String>();

        // When
        List<DummyModel> models = store.getModelsByQueryWithLike(where);

        // Then
        assertNotNull(models);
        assertEquals(0, models.size());
    }

    @SmallTest
    public void testGetModelsByQueryWithLikeReturnsCorrectModel() {
        // Given
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mapper.map(cursor)).thenReturn(model);
        Map<String, String> where = new HashMap<String, String>();

        // When
        List<DummyModel> models = store.getModelsByQuery(where);

        // Then
        assertNotNull(models);
        assertEquals(1, models.size());
        assertEquals(model, models.get(0));
    }


    private class DummyModel extends Model {
        public DummyModel() {
        }

        public DummyModel(String id) {
            super(id);
        }
    }
}
