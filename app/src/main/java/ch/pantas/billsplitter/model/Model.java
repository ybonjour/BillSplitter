package ch.pantas.billsplitter.model;

public abstract class Model {

    private final int id;

    public Model(int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
}
