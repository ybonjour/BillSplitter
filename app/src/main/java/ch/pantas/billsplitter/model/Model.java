package ch.pantas.billsplitter.model;

import java.util.UUID;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public abstract class Model {

    private UUID id;

    public Model(){ }

    public Model(UUID id) {
        checkNotNull(id);
        this.id = id;
    }

    public UUID getId(){
        return this.id;
    }

    public void setId(UUID id) {
        checkNotNull(id);
        this.id = id;
    }

    public boolean isNew() { return id == null; }
}
