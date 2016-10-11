package yio.tro.fluffyhead.tasks;

public class Task {

    String text;
    boolean complete;
    int id;


    public Task(int id) {
        this.id = id;
        complete = false;
    }


    public boolean isComplete() {
        return complete;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "[" + id + ": " + text + "]";
    }
}
