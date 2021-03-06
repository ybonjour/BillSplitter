package ch.pantas.billsplitter.model;

import java.util.UUID;

public class User extends Model {

    private String name;
    private String macAddress;

    public User(UUID id, String name) {
        super(id);
        this.name = name;
    }

    public User(String name){
        this.name = name;
    }

    public User(UUID id, String name, String macAddress) {
        super(id);
        this.name = name;
        this.macAddress = macAddress;
    }

    public User(String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public boolean isConnectedUser() { return macAddress != null; }

    public String getMacAddress() { return macAddress; }

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
