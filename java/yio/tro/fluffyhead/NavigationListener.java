package yio.tro.fluffyhead;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import yio.tro.fluffyhead.notes.NotesActivity;
import yio.tro.fluffyhead.reminders.RemindersActivity;
import yio.tro.fluffyhead.tasks.TasksActivity;

public class NavigationListener implements NavigationView.OnNavigationItemSelectedListener {

    Activity context;
    SmoothActionBarDrawerToggle actionBarDrawerToggle;


    public NavigationListener(Activity context, ActionBarDrawerToggle actionBarDrawerToggle) {
        this.context = context;
        this.actionBarDrawerToggle = (SmoothActionBarDrawerToggle) actionBarDrawerToggle;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tasks) {
            startActivitySmoothly(TasksActivity.class, false);
        } else if (id == R.id.nav_notes) {
            startActivitySmoothly(NotesActivity.class, false);
        } else if (id == R.id.nav_reminders) {
            startActivitySmoothly(RemindersActivity.class, false);
        } else if (id == R.id.nav_settings) {
            startActivitySmoothly(SettingsActivity.class, true);
        } else if (id == R.id.nav_about) {
            startActivitySmoothly(AboutActivity.class, true);
        }


        DrawerLayout drawer = (DrawerLayout) context.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    private void startActivitySmoothly(final Class activityClass, final boolean animation) {
        actionBarDrawerToggle.runWhenIdle(new Runnable() {
            @Override
            public void run() {
                startActivity(activityClass, animation);
            }
        });
    }


    private void startActivity(Class activityClass, boolean animation) {
        Intent intent = new Intent(context, activityClass);
        if (!animation) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        context.startActivity(intent);
    }
}
