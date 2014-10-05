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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if(!(o instanceof User)) return false;

        User user = (User) o;

        if(getId() == null){
            return name.equals(user.getName());
        } else {
            return getId().equals(user.getId());
        }
    }

    @Override
    public int hashCode() {
        if(getId() == null) {
            return name.hashCode();
        } else {
            return getId().hashCode();
        }
    }
}
