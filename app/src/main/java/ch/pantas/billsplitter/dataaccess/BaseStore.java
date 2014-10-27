package ch.pantas.billsplitter.dataaccess;

import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.dataaccess.rowmapper.RowMapper;
import ch.pantas.billsplitter.model.Model;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public abstract class BaseStore<M extends Model> {

    protected GenericStore<M> genericStore;

    public BaseStore(RowMapper<M> mapper, GenericStore<M> genericStore) {
        checkNotNull(mapper);
        this.genericStore = genericStore;
        this.genericStore.setRowMapper(mapper);
    }

    public M getById(String id) {
        return genericStore.getById(id);
    }

    public List<M> getAll() {
        return genericStore.getAll();
    }

    public void persist(M model) {
        genericStore.persist(model);
    }

    public void createExistingModel(M model) {
        genericStore.createExistingModel(model);
    }

    public void removeById(String id) {
        genericStore.removeById(id);
    }

    public void removeAll(Map<String, String> where) {
        genericStore.removeAll(where);
    }
}
