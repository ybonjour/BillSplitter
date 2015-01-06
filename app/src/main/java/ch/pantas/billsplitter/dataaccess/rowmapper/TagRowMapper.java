package ch.pantas.billsplitter.dataaccess.rowmapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

import ch.pantas.billsplitter.model.Tag;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.Table.ID;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.NAME;
import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.TABLE;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class TagRowMapper implements RowMapper<Tag> {
    @Override
    public Tag map(Cursor cursor) {
        checkNotNull(cursor);

        int idxId = cursor.getColumnIndex(ID);
        int idxName = cursor.getColumnIndex(NAME);

        UUID id = UUID.fromString(cursor.getString(idxId));
        String name = cursor.getString(idxName);

        return new Tag(id, name);
    }

    @Override
    public ContentValues getValues(Tag tag) {
        checkNotNull(tag);

        ContentValues values = new ContentValues();
        values.put(ID, tag.getId().toString());
        values.put(NAME, tag.getName());

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
