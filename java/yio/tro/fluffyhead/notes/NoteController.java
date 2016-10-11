package yio.tro.fluffyhead.notes;

import java.util.ArrayList;
import java.util.ListIterator;

public class NoteController {

    private static NoteController instance;
    ArrayList<Note> notes;
    NotesActivity notesActivity;


    public static NoteController getInstance(NotesActivity notesActivity) {
        if (instance == null) {
            instance = new NoteController();
            instance.setNotesActivity(notesActivity);
            instance.loadNotes();
        }

        return instance;
    }


    public NoteController() {
        notes = new ArrayList<>();
    }


    public void setNotesActivity(NotesActivity notesActivity) {
        this.notesActivity = notesActivity;
    }


    public void onNoteTouched(int position) {

    }


    void deleteSelectedNotes(ArrayList<Note> selectedNotes) {
        ListIterator iterator = notes.listIterator();

        while (iterator.hasNext()) {
            Note note = (Note) iterator.next();
            if (selectedNotes.contains(note)) {
                iterator.remove();
                notesActivity.onNoteDelete(note);
            }
        }
    }


    private void loadNotes() {
        notes.clear();

        notes.addAll(notesActivity.getNotesFromDatabase());
    }


    public ArrayList<Note> getNotes() {
        return notes;
    }


    void addNote(String text) {
        Note note = new Note(getIdForNewNote());
        note.setText(text);
        notes.add(note);

        notesActivity.onNoteAdd(note);
    }


    void editNote(Note note, String newText) {
        note.setText(newText);

        notesActivity.onNoteEdited(note);
    }


    int getIdForNewNote() {
        if (notes.size() == 0) return 1;
        return notes.get(notes.size() - 1).id + 1;
    }
}
