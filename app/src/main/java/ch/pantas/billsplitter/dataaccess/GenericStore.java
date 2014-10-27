package ch.pantas.billsplitter.dataaccess;

import android.database.Cursor;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.RowMapper;
import ch.pantas.billsplitter.model.Model;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class GenericStore<M extends Model> {

    @Inject
    private BillSplitterDatabaseOpenHelper dbHelper;

    private RowMapper<M> mapper;

    public void setRowMapper(RowMapper<M> mapper){
        this.mapper = mapper;
    }

    public M getById(String id) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(ID, id);
        List<M> models = getModelsByQuery(where);

        if (models.size() == 0) {
            return null;
        } else {
            return models.get(0);
        }
    }

    public List<M> getAll() {
        return getModelsByQuery(null);
    }

    public void persist(M model) {
        checkNotNull(model);

        BillSplitterDatabase db = dbHelper.getDatabase();

        if (model.isNew()) {
            String id = UUID.randomUUID().toString();
            model.setId(id);
            db.insert(mapper.getTableName(), mapper.getValues(model));
        } else {
            db.update(mapper.getTableName(), mapper.getValues(model));
        }
    }

    public void createExistingModel(M model){
        checkNotNull(model);
        checkArgument(!model.isNew());

        BillSplitterDatabase db = dbHelper.getDatabase();

        db.insert(mapper.getTableName(), mapper.getValues(model));
    }

    public void removeById(String id){
        checkNotNull(id);
        checkArgument(!id.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(ID, id);

        removeAll(where);
    }

    public void removeAll(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        db.removeAll(mapper.getTableName(), where);
    }

    protected List<M> getModelsByQuery(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        Cursor cursor = db.query(mapper.getTableName(), where);
        return toModelList(cursor, mapper);
    }

    protected List<M> getModelsByQueryWithLike(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        Cursor cursor = db.queryWithLike(mapper.getTableName(), where);
        return toModelList(cursor, mapper);
    }

    private static <M extends Model> List<M> toModelList(Cursor cursor, RowMapper<M> mapper) {
        ArrayList<M> models = new ArrayList<M>();
        while (cursor.moveToNext()) {
            M model = mapper.map(cursor);
            models.add(model);
        }

        return models;
    }
}
