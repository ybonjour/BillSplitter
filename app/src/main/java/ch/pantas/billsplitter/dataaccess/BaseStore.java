package ch.pantas.billsplitter.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.RowMapper;
import ch.pantas.billsplitter.model.Model;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public abstract class BaseStore<M extends Model> {

    private RowMapper<M> mapper;

    @Inject
    private BillSplitterDatabaseOpenHelper dbHelper;

    @Inject
    public BaseStore(RowMapper<M> mapper) {
        checkNotNull(mapper);

        this.mapper = mapper;
    }

    public List<M> getAll(){
        return getModelsByQuery(null);
    }

    protected List<M> getModelsByQuery(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        Cursor cursor = db.query(mapper.getTableName(), where);
        return toModelList(cursor, mapper);
    }

    public void persist(M model){
        BillSplitterDatabase db = dbHelper.getDatabase();

        if(model.isNew()){
            String id = UUID.randomUUID().toString();
            model.setId(id);
            db.insert(mapper.getTableName(), mapper.values(model));
        } else {
            db.update(mapper.getTableName(), mapper.values(model));
        }
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
