package yio.tro.fluffyhead;

import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Toast;
import yio.tro.fluffyhead.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiChoiceYio implements AbsListView.MultiChoiceModeListener {

    protected AbsListView listView;
    int menu_id;


    public MultiChoiceYio(AbsListView listView, int menu_id) {
        this.listView = listView;
        this.menu_id = menu_id;
    }


    @Override
    //Метод вызывается при любом изменении состояния выделения рядов
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        int selectedCount = listView.getCheckedItemCount();
        //Добавим количество выделенных рядов в Context Action Bar
        setSubtitle(actionMode, selectedCount);
    }


    @Override
    //Здесь надуваем CAB из xml
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(menu_id, menu);
        return true;
    }


    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
    }


    private void setSubtitle(ActionMode mode, int selectedCount) {
        switch (selectedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            default:
                mode.setTitle(String.valueOf(selectedCount));
                break;
        }
    }
}
