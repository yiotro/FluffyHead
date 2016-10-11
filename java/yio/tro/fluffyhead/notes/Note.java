package yio.tro.fluffyhead.notes;

public class Note {

    String text;
    int id;


    public Note(int id) {
        this.id = id;
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
}
