package yio.tro.fluffyhead.reminders;

import java.sql.Time;
import java.util.ArrayList;
import java.util.ListIterator;

public class ReminderController {


    private static ReminderController instance;
    ArrayList<Reminder> reminders;
    RemindersActivity remindersActivity;


    public static ReminderController getInstance(RemindersActivity remindersActivity) {
        if (instance == null) {
            instance = new ReminderController();
            instance.setRemindersActivity(remindersActivity);
            instance.loadReminders();
        }

        return instance;
    }


    public ReminderController() {
        reminders = new ArrayList<>();
    }


    public void setRemindersActivity(RemindersActivity remindersActivity) {
        this.remindersActivity = remindersActivity;
    }


    public void onReminderTouched(int position) {

    }


    void deleteSelectedReminders(ArrayList<Reminder> selectedReminders) {
        ListIterator iterator = reminders.listIterator();

        while (iterator.hasNext()) {
            Reminder reminder = (Reminder) iterator.next();
            if (selectedReminders.contains(reminder)) {
                iterator.remove();
                remindersActivity.onReminderDelete(reminder);
            }
        }
    }


    private void loadReminders() {
        reminders.clear();

        reminders.addAll(remindersActivity.getRemindersFromDatabase());
    }


    public ArrayList<Reminder> getReminders() {
        return reminders;
    }


    Reminder addReminder(String text, int hours, int minutes, boolean days[]) {
        Reminder reminder = new Reminder(getIdForNewReminder());
        reminder.setText(text);
        reminder.setHours(hours);
        reminder.setMinutes(minutes);
        reminder.setDays(days);
        reminders.add(reminder);

        remindersActivity.onReminderAdd(reminder);
        return reminder;
    }


    void editReminder(Reminder reminder, String text, int hours, int minutes, boolean days[]) {
        reminder.setText(text);
        reminder.setHours(hours);
        reminder.setMinutes(minutes);
        reminder.setDays(days);

        remindersActivity.onReminderEdit(reminder);
    }


    int getIdForNewReminder() {
        if (reminders.size() == 0) return 1;
        return reminders.get(reminders.size() - 1).id + 1;
    }

}
