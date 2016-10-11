package yio.tro.fluffyhead.notes;

import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import yio.tro.fluffyhead.MultiChoiceYio;
import yio.tro.fluffyhead.R;
import yio.tro.fluffyhead.tasks.Task;

import java.util.ArrayList;

public class NoteMultiChoice extends MultiChoiceYio{

    NotesActivity notesActivity;


    public NoteMultiChoice(AbsListView listView, int menu_id, NotesActivity notesActivity) {
        super(listView, menu_id);
        this.notesActivity = notesActivity;
    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        notesActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        return super.onCreateActionMode(actionMode, menu);
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        super.onDestroyActionMode(actionMode);
        notesActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.cab_task_delete) {
            deleteSelectedNotes();
        }

        mode.finish();

        return false;
    }


    private void deleteSelectedNotes() {
        ArrayList<Note> selectedNotes = new ArrayList<Note>();

        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.valueAt(i)) {
                Note note = (Note) listView.getItemAtPosition(sparseBooleanArray.keyAt(i));
                selectedNotes.add(note);
            }
        }

        NoteController.getInstance(notesActivity).deleteSelectedNotes(selectedNotes);
        notesActivity.refreshViews();
    }
}
