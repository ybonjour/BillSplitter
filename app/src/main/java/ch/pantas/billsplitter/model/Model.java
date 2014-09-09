package ch.pantas.billsplitter.model;

public abstract class Model {

    private final String id;

    public Model(String id) {
        this.id = id;
    }

    public String getId(){
        return this.id;
    }
}
