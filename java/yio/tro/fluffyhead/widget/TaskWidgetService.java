package yio.tro.fluffyhead.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TaskWidgetService extends RemoteViewsService{


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskWidgetFactory(getApplicationContext(), intent);
    }
}
