package yio.tro.fluffyhead.reminders;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reminder implements Serializable{

    int id;
    String text;
    boolean days[];
    int hours, minutes;


    public Reminder(int id) {
        this.id = id;
        days = new boolean[7];
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public int getId() {
        return id;
    }


    public int getHours() {
        return hours;
    }


    public void setHours(int hours) {
        this.hours = hours;
    }


    public int getMinutes() {
        return minutes;
    }


    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }


    public boolean[] getDays() {
        return days;
    }


    public String getDaysString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < days.length; i++) {
            if (days[i]) {
                builder.append("1");
            } else {
                builder.append("0");
            }
        }

        return builder.toString();
    }


    public void setDaysByString(String daysString) {
        days = new boolean[7];
        for (int i = 0; i < days.length; i++) {
            char c = daysString.charAt(i);
            if (c == '1') {
                days[i] = true;
            } else {
                days[i] = false;
            }
        }
    }


    public void setDays(boolean[] days) {
        this.days = new boolean[7];
        for (int i = 0; i < days.length; i++) {
            this.days[i] = days[i];
        }
    }


    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setHours(getHours());
        date.setMinutes(getMinutes());
        String time = simpleDateFormat.format(date);
        return "[" + id + ": (" + time + ") " + text + "]";
    }
}
