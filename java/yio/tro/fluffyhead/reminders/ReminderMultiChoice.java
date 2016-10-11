package yio.tro.fluffyhead.reminders;

import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import yio.tro.fluffyhead.MultiChoiceYio;
import yio.tro.fluffyhead.R;

import java.util.ArrayList;

public class ReminderMultiChoice extends MultiChoiceYio{

    RemindersActivity remindersActivity;


    public ReminderMultiChoice(AbsListView listView, int menu_id, RemindersActivity remindersActivity) {
        super(listView, menu_id);
        this.remindersActivity = remindersActivity;
    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        remindersActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        return super.onCreateActionMode(actionMode, menu);
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        super.onDestroyActionMode(actionMode);
        remindersActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.cab_task_delete) {
            deleteSelectedReminders();
        }

        mode.finish();

        return false;
    }


    private void deleteSelectedReminders() {
        ArrayList<Reminder> selectedReminders = new ArrayList<Reminder>();

        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.valueAt(i)) {
                Reminder reminder = (Reminder) listView.getItemAtPosition(sparseBooleanArray.keyAt(i));
                selectedReminders.add(reminder);
            }
        }

        ReminderController.getInstance(remindersActivity).deleteSelectedReminders(selectedReminders);
        remindersActivity.refreshViews();
    }

}
