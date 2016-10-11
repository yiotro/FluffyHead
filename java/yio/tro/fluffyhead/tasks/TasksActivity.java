package yio.tro.fluffyhead.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import yio.tro.fluffyhead.NavigationListener;
import yio.tro.fluffyhead.R;
import yio.tro.fluffyhead.SmoothActionBarDrawerToggle;
import yio.tro.fluffyhead.database.MyDbHandler;
import yio.tro.fluffyhead.widget.TasksWidgetProvider;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    private String m_Text = "";
    TaskController taskController;
    CustomTasksAdapter adapter;
    ListView listView;
    MyDbHandler myDbHandler;
    AlertDialog inputDialog;
    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFloatActionButton();
        initNavigationDrawer(toolbar);
        initBarTitle();

        myDbHandler = new MyDbHandler(this, null);

        taskController = TaskController.getInstance(this);
        initListView();

        sendBroadcastToUpdateWidget();
        refreshEmptySign();
    }


    private void initBarTitle() {
        getSupportActionBar().setTitle(R.string.tasks);
    }


    private void initNavigationDrawer(Toolbar toolbar) {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        SmoothActionBarDrawerToggle drawerToggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationListener(this, drawerToggle));
    }


    private void initFloatActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputDialog();
            }
        });
    }


    private void initListView() {
        adapter = new CustomTasksAdapter(this, taskController.getTasks());
        listView = (ListView) findViewById(R.id.list_view_tasks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean checked = taskController.onTaskPressed(position);

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.task_row_checkbox);
                checkBox.setChecked(checked);
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new TaskMultiChoice(listView, R.menu.context_task_menu, this));
    }


    void refreshViews() {
        adapter.notifyDataSetChanged();
    }


    public void sendBroadcastToUpdateWidget() {
        TasksWidgetProvider.updateWidgets(this);
    }


    private void refreshEmptySign() {
        boolean isEmpty = taskController.getTasks().size() == 0;
        TextView emptySign = (TextView) findViewById(R.id.task_empty);
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


    @Override
    protected void onStop() {
        super.onStop();
        taskController.clearCompletedTasks();
        refreshViews();
    }


    void onTaskDelete(Task task) {
        myDbHandler.deleteTask(task);
        sendBroadcastToUpdateWidget();
        refreshEmptySign();
    }


    void onTaskAdd(Task task) {
        myDbHandler.addTask(task);
        sendBroadcastToUpdateWidget();
        refreshEmptySign();
    }


    void onTaskEdit(Task task) {
        myDbHandler.editTask(task);
        sendBroadcastToUpdateWidget();
        refreshEmptySign();
        Log.d("yiotro", myDbHandler.databaseToString());
    }


    ArrayList<Task> getTasksFromDatabase() {
        return myDbHandler.getTasks();
    }


    private void createInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_task);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onInputDialogOkButton(input);
                    inputDialog.cancel();
                }
                return true;
            }
        });
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onInputDialogOkButton(input);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        inputDialog = builder.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void onInputDialogOkButton(EditText input) {
        m_Text = input.getText().toString();
        if (m_Text.length() == 0) return;
        taskController.addTask(m_Text);
    }


    void createEditDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_task);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(task.getText());
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onEditInputDialogOkButton(input, task);
                    inputDialog.cancel();
                }
                return true;
            }
        });
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onEditInputDialogOkButton(input, task);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        inputDialog = builder.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void onEditInputDialogOkButton(EditText input, Task task) {
        m_Text = input.getText().toString();
        if (m_Text.length() == 0) return;
        taskController.editTask(task, m_Text);
    }
}
