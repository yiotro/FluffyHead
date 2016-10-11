package yio.tro.fluffyhead.tasks;

import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import yio.tro.fluffyhead.MultiChoiceYio;
import yio.tro.fluffyhead.R;

import java.util.ArrayList;
import java.util.List;

public class TaskMultiChoice extends MultiChoiceYio{

    TasksActivity tasksActivity;
    ArrayList<Task> selectedTasks;


    public TaskMultiChoice(AbsListView listView, int menu_id, TasksActivity tasksActivity) {
        super(listView, menu_id);
        this.tasksActivity = tasksActivity;
    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        tasksActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        return super.onCreateActionMode(actionMode, menu);
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        super.onDestroyActionMode(actionMode);
        tasksActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    @Override
    //Вызывается при клике на любой Item из СAB
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        createSelectedTasks();

        if (menuItem.getItemId() == R.id.cab_task_delete) {
            deleteSelectedTasks();
        }

        if (menuItem.getItemId() == R.id.cab_task_edit) {
            editSelectedTask();
        }

        actionMode.finish();

        return false;
    }


    private void editSelectedTask() {
        if (selectedTasks.size() != 1) return;

        tasksActivity.createEditDialog(selectedTasks.get(0));
    }


    private void createSelectedTasks() {
        selectedTasks = new ArrayList<Task>();

        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.valueAt(i)) {
                Task task = (Task) listView.getItemAtPosition(sparseBooleanArray.keyAt(i));
                selectedTasks.add(task);
            }
        }
    }


    private void deleteSelectedTasks() {
        TaskController.getInstance(tasksActivity).deleteSelectedTasks(selectedTasks);
        tasksActivity.refreshViews();
    }
}
