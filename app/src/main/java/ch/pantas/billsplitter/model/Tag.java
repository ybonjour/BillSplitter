package ch.pantas.billsplitter.model;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class Tag extends Model {

    private final String name;

    public Tag(String id, String name) {
        super(id);
        checkNotNull(name);

        this.name = name;
    }

    public Tag(String name){
        checkNotNull(name);

        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (!name.equals(tag.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
