package yio.tro.fluffyhead.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import yio.tro.fluffyhead.R;
import yio.tro.fluffyhead.tasks.TasksActivity;

import java.util.ArrayList;

public class TaskWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    ArrayList<String> data;
    Context context;
    int widgetID;


    @TargetApi(24)
    TaskWidgetFactory(Context ctx, Intent intent) {
        context = ctx;
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {
        data = new ArrayList<String>();
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(context.getPackageName(),
                R.layout.widget_row);
        rView.setTextViewText(R.id.widget_row_text_view, data.get(position));

        Intent clickIntent = new Intent(context, TasksActivity.class);
//        clickIntent.putExtra(TasksWidgetProvider.ITEM_POSITION, position);

//        Intent intent = new Intent(context, TasksActivity.class);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
        rView.setOnClickFillInIntent(R.id.widget_row_text_view, clickIntent);

        return rView;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }


    @TargetApi(24)
    @Override
    public void onDataSetChanged() {
        if (TasksWidgetProvider.taskStringList == null) {
            Log.d("yiotro", "task list is null");
            return;
        }

        data.clear();

        for (String s : TasksWidgetProvider.taskStringList) {
            data.add(s);
        }
    }


    @Override
    public void onDestroy() {

    }
}
