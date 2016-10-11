package yio.tro.fluffyhead.reminders;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import yio.tro.fluffyhead.NavigationListener;
import yio.tro.fluffyhead.R;
import yio.tro.fluffyhead.SmoothActionBarDrawerToggle;
import yio.tro.fluffyhead.database.MyDbHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class RemindersActivity extends AppCompatActivity {

    ReminderController reminderController;
    private String m_Text = "";
    MyDbHandler myDbHandler;
    CustomReminderAdapter adapter;
    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFloatingActionButton();

        initNavigationView(toolbar);

        getSupportActionBar().setTitle(R.string.reminders);

        myDbHandler = new MyDbHandler(this, null);

        reminderController = ReminderController.getInstance(this);

        initListView();
        refreshEmptySign();
    }


    private void initNavigationView(Toolbar toolbar) {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        SmoothActionBarDrawerToggle drawerToggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationListener(this, drawerToggle));
    }


    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputDialog();
            }
        });
    }


    void sendTestNotification() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker("This is some ticker, biatch!");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Wow. Title");
        notification.setContentText("This is the body text, biatch!");

        Intent intent = new Intent(this, RemindersActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(69, notification.build());
    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    private void initListView() {
        adapter = new CustomReminderAdapter(this, reminderController.getReminders());
        ListView listView = (ListView) findViewById(R.id.list_view_reminders);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder reminder = reminderController.getReminders().get(position);
                createEditDialog(reminder);
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ReminderMultiChoice(listView, R.menu.context_reminder_menu, this));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    void refreshViews() {
//        adapter.clear();
//        adapter.addAll(taskController.getTasks());
        adapter.notifyDataSetChanged();
//        listView.invalidateViews();
//        listView.refreshDrawableState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    void onReminderDelete(Reminder reminder) {
        myDbHandler.deleteReminder(reminder);

        updateAllAlarms();
        refreshEmptySign();
    }


    void onReminderAdd(Reminder reminder) {
        myDbHandler.addReminder(reminder);
        refreshEmptySign();
    }


    void onReminderEdit(Reminder reminder) {
        myDbHandler.editReminder(reminder);
        refreshEmptySign();
    }


    ArrayList<Reminder> getRemindersFromDatabase() {
        return myDbHandler.getReminders();
    }


    private void createInputDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.reminder_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_reminder);

        builder.setView(dialogView);

        final EditText input = (EditText) dialogView.findViewById(R.id.reminder_edit_text);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addReminder(dialogView);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.rem_time_picker);
        timePicker.setIs24HourView(true);

        setToggleButtons(dialogView);

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    @TargetApi(Build.VERSION_CODES.M)
    void createEditDialog(final Reminder reminder) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.reminder_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_reminder);

        builder.setView(dialogView);

        final EditText input = (EditText) dialogView.findViewById(R.id.reminder_edit_text);
        input.setText(reminder.getText());

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editReminder(reminder, dialogView);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.rem_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(reminder.getHours());
        timePicker.setCurrentMinute(reminder.getMinutes());

        setToggleButtons(dialogView);
        boolean[] days = reminder.getDays();
        for (int i = 0; i < days.length; i++) {
            ToggleButton toggleButton = (ToggleButton) dialogView.findViewById(getToggleButtonId(i));
            toggleButton.setChecked(days[i]);
        }
    }


    private void addReminder(View dialogView) {
        EditText input = (EditText) dialogView.findViewById(R.id.reminder_edit_text);
        m_Text = input.getText().toString();
        if (m_Text.length() == 0) return;
        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.rem_time_picker);
        boolean days[] = getDays(dialogView);
        Reminder reminder = reminderController.addReminder(m_Text, timePicker.getCurrentHour(), timePicker.getCurrentMinute(), days);
        refreshViews();

        updateAllAlarms();
    }


    public void notifyAtTime(Reminder reminder) {
        Log.d("yiotro", "notify at time: " + reminder);

        Intent myIntent = new Intent(RemindersActivity.this , ReminderNotificationService.class);
        myIntent.putExtra("reminder", reminder);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent alarmPendingIntent = getAlarmPendingIntent("" + reminder.getId(), reminder);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHours());
        calendar.set(Calendar.MINUTE, reminder.getMinutes());
        calendar.set(Calendar.SECOND, 0);
        if (isAlarmBeforeCurrentTime(reminder)) {
            Date currentDate = new Date();
            int nextDay = currentDate.getDay() + 1;
            if (nextDay == 8) nextDay = 1;
            calendar.set(Calendar.DAY_OF_WEEK, nextDay);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , alarmPendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5*60*1000 , alarmPendingIntent);
    }


    private boolean isAlarmBeforeCurrentTime(Reminder reminder) {
        Date currentDate = new Date();
        if (reminder.getHours() > currentDate.getHours()) return false;
        if (reminder.getHours() < currentDate.getHours()) return true;
        if (reminder.getMinutes() > currentDate.getMinutes()) return false;
        return true;
    }


    private void refreshEmptySign() {
        boolean isEmpty = reminderController.getReminders().size() == 0;
        TextView emptySign = (TextView) findViewById(R.id.reminder_empty);
        if (isEmpty) {
            emptySign.setVisibility(View.VISIBLE);
        } else {
            emptySign.setVisibility(View.INVISIBLE);
        }
    }


    public void updateAllAlarms() {
        cancelAlarms();
        updateIdPreferences();
        for (Reminder reminder : reminderController.getReminders()) {
            notifyAtTime(reminder);
        }
    }


    private void updateIdPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
        sharedPreferences.edit().putString("ids", getIdString()).apply();
    }


    public void cancelAlarms() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
        String idString = sharedPreferences.getString("ids", "");
        ArrayList<String> alarmIds = getAlarmIds(idString);

        for (String alarmId : alarmIds) {
            Log.d("yiotro", "cancelling alarm: " + alarmId);
            PendingIntent alarmPendingIntent = getAlarmPendingIntent(alarmId, null);
            alarmManager.cancel(alarmPendingIntent);
        }
    }


    private PendingIntent getAlarmPendingIntent(String alarmId, Reminder reminder) {
        Intent myIntent = new Intent(RemindersActivity.this , ReminderNotificationService.class);
        myIntent.setData(Uri.parse(alarmId));
        if (reminder != null) myIntent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getService(RemindersActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    String getIdString() {
        StringBuilder builder = new StringBuilder();
        for (Reminder reminder : reminderController.getReminders()) {
            builder.append(reminder.getId() + " ");
        }
        return builder.toString();
    }


    ArrayList<String> getAlarmIds(String idString) {
        ArrayList<String> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(idString, " ");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            result.add(token);
        }
        return result;
    }


    private void editReminder(Reminder reminder, View dialogView) {
        EditText input = (EditText) dialogView.findViewById(R.id.reminder_edit_text);
        m_Text = input.getText().toString();
        if (m_Text.length() == 0) return;
        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.rem_time_picker);
        boolean days[] = getDays(dialogView);
        reminderController.editReminder(reminder, m_Text, timePicker.getCurrentHour(), timePicker.getCurrentMinute(), days);
        refreshViews();

        updateAllAlarms();
    }


    boolean[] getDays(View view) {
        boolean days[] = new boolean[7];
        for (int i = 0; i < days.length; i++) {
            ToggleButton toggleButton = (ToggleButton) view.findViewById(getToggleButtonId(i));
            days[i] = toggleButton.isChecked();
        }
        return days;
    }


    private void setToggleButtons(View dialogView) {
        for (int i = 0; i < 7; i++) {
            setToggleButton(dialogView, getToggleButtonId(i));
        }
    }


    private int getToggleButtonId(int index) {
        switch (index) {
            default:
            case 0: return R.id.rem_mon;
            case 1: return R.id.rem_tue;
            case 2: return R.id.rem_wed;
            case 3: return R.id.rem_thu;
            case 4: return R.id.rem_fri;
            case 5: return R.id.rem_sat;
            case 6: return R.id.rem_sun;
        }
    }


    private void setToggleButton(View dialogView, int id) {
        final ToggleButton toggleButton = (ToggleButton) dialogView.findViewById(id);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleButton.setTextColor(Color.parseColor("#FF5C6BC0"));
                } else {
                    toggleButton.setTextColor(Color.BLACK);
                }
            }
        });
    }
}
