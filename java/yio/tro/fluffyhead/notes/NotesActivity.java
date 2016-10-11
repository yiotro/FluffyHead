package yio.tro.fluffyhead.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import yio.tro.fluffyhead.NavigationListener;
import yio.tro.fluffyhead.R;
import yio.tro.fluffyhead.SmoothActionBarDrawerToggle;
import yio.tro.fluffyhead.database.MyDbHandler;
import yio.tro.fluffyhead.notes.CustomNotesAdapter;
import yio.tro.fluffyhead.tasks.Task;
import yio.tro.fluffyhead.tasks.TaskMultiChoice;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    NoteController noteController;
    private String m_Text = "";
    CustomNotesAdapter adapter;
    MyDbHandler myDbHandler;
    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFloatingActionButton();

        initNavigationView(toolbar);

        getSupportActionBar().setTitle(R.string.notes);

        myDbHandler = new MyDbHandler(this, null);

        noteController = NoteController.getInstance(this);

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


    private void initListView() {
        adapter = new CustomNotesAdapter(this, noteController.getNotes());
        ListView listView = (ListView) findViewById(R.id.list_view_notes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = noteController.getNotes().get(position);
                createEditDialog(note);
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new NoteMultiChoice(listView, R.menu.context_note_menu, this));
    }


    void refreshViews() {
//        adapter.clear();
//        adapter.addAll(taskController.getTasks());
        adapter.notifyDataSetChanged();
//        listView.invalidateViews();
//        listView.refreshDrawableState();
    }


    private void refreshEmptySign() {
        boolean isEmpty = noteController.getNotes().size() == 0;
        TextView emptySign = (TextView) findViewById(R.id.note_empty);
        if (isEmpty) {
            emptySign.setVisibility(View.VISIBLE);
        } else {
            emptySign.setVisibility(View.INVISIBLE);
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    void onNoteDelete(Note note) {
        myDbHandler.deleteNote(note);
        refreshEmptySign();
    }


    void onNoteAdd(Note note) {
        myDbHandler.addNote(note);
        refreshEmptySign();
    }


    void onNoteEdited(Note note) {
        myDbHandler.editNote(note);
        refreshViews();
        refreshEmptySign();
    }


    ArrayList<Note> getNotesFromDatabase() {
        return myDbHandler.getNotes();
    }


    private void createInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_note);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if (m_Text.length() == 0) return;
                noteController.addNote(m_Text);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void createEditDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_note);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(note.getText());
        input.setSingleLine(false);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if (m_Text.length() == 0) return;
                noteController.editNote(note, m_Text);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
