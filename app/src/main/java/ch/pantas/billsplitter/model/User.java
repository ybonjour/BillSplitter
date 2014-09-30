package ch.pantas.billsplitter.model;

public class User extends Model {

    private final String name;

    public User(String id, String name) {
        super(id);
        this.name = name;
    }

    public User(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
