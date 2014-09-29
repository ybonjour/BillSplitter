package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.model.Model;

public interface RowMapper<M extends Model> {

    public M map(Cursor cursor);
    public ContentValues getValues(M model);
    public String getTableName();
}
