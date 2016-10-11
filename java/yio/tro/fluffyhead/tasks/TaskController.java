package yio.tro.fluffyhead.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import yio.tro.fluffyhead.database.MyDbHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;

public class TaskController {

    private static TaskController instance;

    TasksActivity tasksActivity;
    ArrayList<Task> tasks;


    public static TaskController getInstance(TasksActivity tasksActivity) {
        if (instance == null) {
            instance = new TaskController();
            instance.setTasksActivity(tasksActivity);
            instance.loadTasks();
        }

        return instance;
    }


    private TaskController() {
        tasks = new ArrayList<>();
    }


    boolean onTaskPressed(int position) {
        Task task = getTask(position);
        task.complete = !task.complete;
        return task.complete;
    }


    public void clearCompletedTasks() {
        ListIterator iterator = tasks.listIterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (task.isComplete()) {
                iterator.remove();
                tasksActivity.onTaskDelete(task);
            }
        }
    }


    Task getTask(int index) {
        return tasks.get(index);
    }


    public ArrayList<Task> getTasks() {
        return tasks;
    }


    void addTask(String text) {
        Task task = new Task(getIdForNewTask());
        task.setText(text);
        tasks.add(task);
        sortTasks(tasksActivity, tasks);

        tasksActivity.onTaskAdd(task);
    }


    void editTask(Task task, String newText) {
        task.setText(newText);
        sortTasks(tasksActivity, tasks);

        tasksActivity.onTaskEdit(task);
    }


    public static void sortTasks(Context context, ArrayList<Task> tasks) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean prefSort = defaultSharedPreferences.getBoolean("general_pref_sort", true);
        if (!prefSort) return;

        TaskComparator taskComparator = new TaskComparator();

        Collections.sort(tasks, taskComparator);
    }


    int getIdForNewTask() {
        if (tasks.size() == 0) return 1;
        int max = -1;
        for (Task task : tasks) {
            if (max == -1 || task.getId() > max) {
                max = task.getId();
            }
        }
        return max + 1;
    }


    void deleteSelectedTasks(ArrayList<Task> selectedTasks) {
        ListIterator iterator = tasks.listIterator();

        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (selectedTasks.contains(task)) {
                iterator.remove();
                tasksActivity.onTaskDelete(task);
            }
        }
    }


    void loadTasks() {
        tasks.clear();

        tasks.addAll(tasksActivity.getTasksFromDatabase());
    }


    public void setTasksActivity(TasksActivity tasksActivity) {
        this.tasksActivity = tasksActivity;
    }
}
