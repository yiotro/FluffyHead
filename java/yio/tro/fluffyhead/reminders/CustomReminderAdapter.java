package yio.tro.fluffyhead.reminders;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import yio.tro.fluffyhead.R;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class CustomReminderAdapter extends ArrayAdapter<Reminder> {


    public CustomReminderAdapter(Context context, ArrayList<Reminder> reminders) {
        super(context, R.layout.custom_reminder_row, reminders);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_reminder_row, parent, false);

        Reminder reminder = getItem(position);

        TextView textView = (TextView) customView.findViewById(R.id.custom_reminder_row_text);
        textView.setText(reminder.getText());

        TextView timeView = (TextView) customView.findViewById(R.id.rem_row_time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setHours(reminder.getHours());
        date.setMinutes(reminder.getMinutes());
        timeView.setText(simpleDateFormat.format(date));

        TextView daysView = (TextView) customView.findViewById(R.id.rem_row_days);
        daysView.setText(generateDays(reminder.getDays()));

        return customView;
    }


    private String generateDays(boolean days[]) {
        StringBuilder builder = new StringBuilder();

        ArrayList<String> shorts = new ArrayList<>();

        for (int i = 0; i < days.length; i++) {
            if (!days[i]) continue;
            shorts.add(getContext().getResources().getString(getDay(i)));
        }

        for (int i = 0; i < shorts.size(); i++) {
            String s = shorts.get(i);
            builder.append(s);
            if (i != shorts.size() - 1) {
                builder.append(", ");
            }
        }

        if (builder.length() == 0) {
            builder.append(getContext().getResources().getString(R.string.never));
        }

        return builder.toString();
    }


    private int getDay(int index) {
        switch (index) {
            default:
            case 0: return R.string.monday_short;
            case 1: return R.string.tuesday_short;
            case 2: return R.string.wednesday_short;
            case 3: return R.string.thursday_short;
            case 4: return R.string.friday_short;
            case 5: return R.string.saturday_short;
            case 6: return R.string.sunday_short;
        }
    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
