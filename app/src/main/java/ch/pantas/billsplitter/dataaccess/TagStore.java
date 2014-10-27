package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.TagRowMapper;
import ch.pantas.billsplitter.model.Attendee;
import ch.pantas.billsplitter.model.Tag;

import static ch.pantas.billsplitter.dataaccess.db.BillSplitterDatabaseOpenHelper.TagTable.NAME;
import static com.google.inject.Key.get;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static roboguice.RoboGuice.getInjector;

@Singleton
public class TagStore extends BaseStore<Tag> {

    @Inject
    public TagStore(TagRowMapper mapper, GenericStore<Tag> genericStore) {
        super(mapper, genericStore);
    }

    public Tag getTagWithName(String name) {
        checkNotNull(name);
        checkArgument(!name.isEmpty());

        Map<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        List<Tag> tags = genericStore.getModelsByQuery(where);
        if (tags.size() == 0) {
            return null;
        } else {
            return tags.get(0);
        }
    }

    public List<Tag> getTagsWithNameLike(String name){
        checkNotNull(name);

        HashMap<String, String> where = new HashMap<String, String>();
        where.put(NAME, name);

        return genericStore.getModelsByQueryWithLike(where);
    }
}
