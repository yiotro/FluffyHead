package yio.tro.fluffyhead.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import yio.tro.fluffyhead.R;

import java.util.ArrayList;

public class CustomNotesAdapter extends ArrayAdapter<Note> {


    public CustomNotesAdapter(Context context, ArrayList<Note> notes) {
        super(context, R.layout.custom_note_row, notes);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_note_row, parent, false);

        Note note = getItem(position);

        TextView textView = (TextView) customView.findViewById(R.id.custom_note_row_text);
        textView.setText(note.getText());

        return customView;
    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
