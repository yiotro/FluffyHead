package yio.tro.fluffyhead.tasks;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import yio.tro.fluffyhead.R;

import java.util.ArrayList;

public class CustomTasksAdapter extends ArrayAdapter<Task> {


    public CustomTasksAdapter(Context context, ArrayList<Task> tasks) {
        super(context, R.layout.custom_task_row, tasks);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_task_row, parent, false);

        Task task = getItem(position);
        CheckBox checkBox = (CheckBox) customView.findViewById(R.id.task_row_checkbox);

        checkBox.setText(task.getText());
        checkBox.setChecked(task.isComplete());

        return customView;
    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
