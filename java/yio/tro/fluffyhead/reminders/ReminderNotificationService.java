package yio.tro.fluffyhead.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import yio.tro.fluffyhead.R;

import java.util.Calendar;

public class ReminderNotificationService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.d("yiotro", "on start command: intent is null");
            return START_NOT_STICKY;
        }
        Reminder reminder = (Reminder) intent.getSerializableExtra("reminder");

        String s = "reminder is null.";
        if (reminder != null) s = reminder.toString() + ". ";
        Log.d("yiotro", "on start command notification. " + s + intent.getDataString());

        if (!checkDayOfTheWeek(reminder)) {
            Log.d("yiotro", "cancelled notification because of day of the week: " + reminder.getText());
            return START_NOT_STICKY;
        }

        Log.d("yiotro", "sending notification: " + reminder.getText());

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker(reminder.getText());
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(reminder.getText());
//        notification.setContentText(getString(R.string.swipe_to_dismiss));

        Intent contentIntent = new Intent(this, RemindersActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(3129369, notification.build());

        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(500);

        return START_STICKY;
    }


    private boolean checkDayOfTheWeek(Reminder reminder) {
        boolean days[] = reminder.getDays();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY: return days[0];
            case Calendar.TUESDAY: return days[1];
            case Calendar.WEDNESDAY: return days[2];
            case Calendar.THURSDAY: return days[3];
            case Calendar.FRIDAY: return days[4];
            case Calendar.SATURDAY: return days[5];
            case Calendar.SUNDAY: return days[6];
        }

        return false;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
