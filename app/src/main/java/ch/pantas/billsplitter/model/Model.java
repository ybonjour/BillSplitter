package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public abstract class Model {

    private String id;

    public Model(){ }

    public Model(String id) {
        checkNotNull(id);
        checkArgument(!id.isEmpty());
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        checkNotNull(id);
        checkArgument(!id.isEmpty());
        this.id = id;
    }

    public boolean isNew() { return id == null; }
}
