package ch.pantas.billsplitter.dataaccess;

import android.database.Cursor;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabase;
import ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper;
import ch.pantas.billsplitter.dataaccess.rowmapper.RowMapper;
import ch.pantas.billsplitter.model.Model;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class BaseStore<M extends Model> {

    private RowMapper<M> mapper;

    @Inject
    private BillSplitterDatabaseOpenHelper dbHelper;

    @Inject
    public BaseStore(RowMapper<M> mapper){
        checkNotNull(mapper);

        this.mapper = mapper;
    }

    protected List<M> getModelsByQuery(String sql, String[] arguments){
        BillSplitterDatabase db = dbHelper.getDatabase();
        Cursor cursor = db.rawQuery(sql, arguments);

        ArrayList<M> models = new ArrayList<M>();
        while(cursor.moveToNext()) {
            M model = mapper.map(cursor);
            models.add(model);
        }

        return models;
    }

}
