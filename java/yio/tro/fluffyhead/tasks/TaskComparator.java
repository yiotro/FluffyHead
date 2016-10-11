package yio.tro.fluffyhead.tasks;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {
        return task1.getText().compareToIgnoreCase(task2.getText());
    }
}
