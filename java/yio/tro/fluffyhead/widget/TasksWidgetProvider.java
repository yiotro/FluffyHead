package yio.tro.fluffyhead.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import yio.tro.fluffyhead.R;
import yio.tro.fluffyhead.database.MyDbHandler;
import yio.tro.fluffyhead.tasks.Task;
import yio.tro.fluffyhead.tasks.TaskController;
import yio.tro.fluffyhead.tasks.TasksActivity;

import java.util.ArrayList;

@TargetApi(24)
public class TasksWidgetProvider extends AppWidgetProvider {


    public static final String ACTION_UPDATE_TASKS = "yiotro.UPDATE_TASKS";
    static ArrayList<String> taskStringList;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        separateOnUpdate(context, appWidgetManager, appWidgetIds);
    }


    private void separateOnUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        readTasksFromDatabase(context);

        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }


    void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        setUpdateTitle(rv, context, appWidgetId);
        setList(rv, context, appWidgetId);
        setShortcut(rv, context, appWidgetId);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
    }


    void setShortcut(RemoteViews remoteViews, Context context, int appWidgetId) {
        Intent intent = new Intent(context, TasksActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);  // Identifies the particular widget...
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_shortcut, pendIntent);
    }


    void setUpdateTitle(RemoteViews rv, Context context, int appWidgetId) {
        rv.setTextViewText(R.id.widget_title, context.getResources().getText(R.string.tasks));
    }


    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent intent = new Intent(context, TaskWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME));
        intent.setData(data);
        rv.setRemoteAdapter(R.id.widget_list_view, intent);
    }


    private void prepareTaskList() {
        if (taskStringList == null) {
            taskStringList = new ArrayList<>();
        }

        taskStringList.clear();
    }


    private void readTasksFromDatabase(Context context) {
        prepareTaskList();

        MyDbHandler myDbHandler = new MyDbHandler(context, null);
        ArrayList<Task> tasks = myDbHandler.getTasks();
        TaskController.sortTasks(context, tasks);

        for (Task task : tasks) {
            taskStringList.add(task.getText());
        }
    }


    public static void updateWidgets(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), TasksWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, TasksWidgetProvider.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
