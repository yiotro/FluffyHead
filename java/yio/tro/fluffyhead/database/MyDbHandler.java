package yio.tro.fluffyhead.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import yio.tro.fluffyhead.notes.Note;
import yio.tro.fluffyhead.reminders.Reminder;
import yio.tro.fluffyhead.tasks.Task;

import java.util.ArrayList;

public class MyDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "fluffy.db";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_REMINDERS = "reminders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK_TEXT = "_text";
    public static final String COLUMN_NOTE_TEXT = "_text";
    public static final String COLUMN_REMINDER_TEXT = "_text";
    public static final String COLUMN_REMINDER_HOURS = "_hours";
    public static final String COLUMN_REMINDER_MINUTES = "_minutes";
    public static final String COLUMN_REMINDER_DAYS = "_days";


    public MyDbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    public void addTask(Task task) {
        Log.d("yiotro", "task add: " + task.getId() + " " + task.getText());
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, task.getId());
        values.put(COLUMN_TASK_TEXT, task.getText());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }


    public void editTask(Task task) {
        Log.d("yiotro", "task edit: " + task.getId() + " " + task.getText());
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TEXT, task.getText());
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{task.getId() + ""});
        db.close();
    }


    public void deleteTask(Task task) {
        Log.d("yiotro", "task delete: " + task.getText());
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_TASKS + " where " + COLUMN_ID + "=\"" + task.getId() + "\";");
    }


    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + TABLE_TASKS;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Task task = new Task(c.getInt(c.getColumnIndex(COLUMN_ID)));
            task.setText(c.getString(c.getColumnIndex(COLUMN_TASK_TEXT)));
            result.add(task);
            c.moveToNext();
        }

        return result;
    }


    public String databaseToString() {
        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + TABLE_TASKS;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String currentString = c.getInt(c.getColumnIndex(COLUMN_ID)) + " " + c.getString(c.getColumnIndex(COLUMN_TASK_TEXT));
            if (currentString == null) continue;
            dbString.append(currentString);
            dbString.append("\n");
            c.moveToNext();
        }

        return dbString.toString();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTasks = "create table " + TABLE_TASKS + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_TASK_TEXT + " text" +
                ");";
        db.execSQL(queryTasks);

        String queryNotes = "create table " + TABLE_NOTES + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_NOTE_TEXT + " text" +
                ");";
        db.execSQL(queryNotes);

        String queryReminders = "create table " + TABLE_REMINDERS + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_REMINDER_TEXT + " text," +
                COLUMN_REMINDER_HOURS + " integer," +
                COLUMN_REMINDER_MINUTES + " integer," +
                COLUMN_REMINDER_DAYS + " text" +
                ");";
        db.execSQL(queryReminders);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TASKS);
        db.execSQL("drop table if exists " + TABLE_NOTES);
        db.execSQL("drop table if exists " + TABLE_REMINDERS);
        onCreate(db);
    }


    public void addNote(Note note) {
        Log.d("yiotro", "note add: " + note.getId() + " " + note.getText());
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, note.getId());
        values.put(COLUMN_NOTE_TEXT, note.getText());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }


    public void editNote(Note note) {
        Log.d("yiotro", "note edit: " + note.getId() + " " + note.getText());
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TEXT, note.getText());
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String[]{note.getId() + ""});
        db.close();
    }


    public void deleteNote(Note note) {
        Log.d("yiotro", "note delete: " + note.getText());
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_NOTES + " where " + COLUMN_ID + "=\"" + note.getId() + "\";");
    }


    public ArrayList<Note> getNotes() {
        ArrayList<Note> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + TABLE_NOTES;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Note note = new Note(c.getInt(c.getColumnIndex(COLUMN_ID)));
            note.setText(c.getString(c.getColumnIndex(COLUMN_NOTE_TEXT)));
            result.add(note);
            c.moveToNext();
        }

        return result;
    }


    public void addReminder(Reminder reminder) {
        Log.d("yiotro", "reminder add: " + reminder);
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, reminder.getId());
        values.put(COLUMN_REMINDER_TEXT, reminder.getText());
        values.put(COLUMN_REMINDER_HOURS, reminder.getHours());
        values.put(COLUMN_REMINDER_MINUTES, reminder.getMinutes());
        values.put(COLUMN_REMINDER_DAYS, reminder.getDaysString());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_REMINDERS, null, values);
        db.close();
    }


    public void editReminder(Reminder reminder) {
        Log.d("yiotro", "reminder edit: " + reminder);
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TEXT, reminder.getText());
        values.put(COLUMN_REMINDER_HOURS, reminder.getHours());
        values.put(COLUMN_REMINDER_MINUTES, reminder.getMinutes());
        values.put(COLUMN_REMINDER_DAYS, reminder.getDaysString());
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_REMINDERS, values, COLUMN_ID + "=?", new String[]{reminder.getId() + ""});
        db.close();
    }


    public void deleteReminder(Reminder reminder) {
        Log.d("yiotro", "reminder delete: " + reminder);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_REMINDERS + " where " + COLUMN_ID + "=\"" + reminder.getId() + "\";");
    }


    public ArrayList<Reminder> getReminders() {
        ArrayList<Reminder> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from " + TABLE_REMINDERS;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Reminder reminder = new Reminder(c.getInt(c.getColumnIndex(COLUMN_ID)));
            reminder.setText(c.getString(c.getColumnIndex(COLUMN_REMINDER_TEXT)));
            reminder.setHours(c.getInt(c.getColumnIndex(COLUMN_REMINDER_HOURS)));
            reminder.setMinutes(c.getInt(c.getColumnIndex(COLUMN_REMINDER_MINUTES)));
            reminder.setDaysByString(c.getString(c.getColumnIndex(COLUMN_REMINDER_DAYS)));
            result.add(reminder);
            c.moveToNext();
        }

        return result;
    }
}
