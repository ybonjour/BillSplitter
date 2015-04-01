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

    public M getById(UUID id) {
        Map<String, String> where = new HashMap<String, String>();
        where.put(ID, id.toString());
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

        try {
            if (model.isNew()) {
                UUID id = UUID.randomUUID();
                model.setId(id);
                db.insert(mapper.getTableName(), mapper.getValues(model));
            } else {
                db.update(mapper.getTableName(), mapper.getValues(model));
            }
        } finally {
            db.close();
        }
    }

    public void createExistingModel(M model){
        checkNotNull(model);
        checkArgument(!model.isNew());

        BillSplitterDatabase db = dbHelper.getDatabase();

        try {
            db.insert(mapper.getTableName(), mapper.getValues(model));
        }
        finally {
                db.close();
        }
    }

    public void removeById(UUID id){
        checkNotNull(id);

        Map<String, String> where = new HashMap<String, String>();
        where.put(ID, id.toString());

        removeAll(where);
    }

    public void removeAll(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        try {
            db.removeAll(mapper.getTableName(), where);
        } finally {
            db.close();
        }
    }

    protected List<M> getModelsByQuery(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        try {
            Cursor cursor = db.query(mapper.getTableName(), where);
            try {
                return toModelList(cursor, mapper);
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    protected List<M> getModelsByQueryWithLike(Map<String, String> where) {
        BillSplitterDatabase db = dbHelper.getDatabase();
        try {
            Cursor cursor = db.queryWithLike(mapper.getTableName(), where);
            try {
                return toModelList(cursor, mapper);
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
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
