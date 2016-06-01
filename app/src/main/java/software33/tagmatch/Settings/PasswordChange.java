package software33.tagmatch.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.R;
import software33.tagmatch.Utils.NavigationController;

public class PasswordChange extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_password_change);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_password_change);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_password_change);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_password_change);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePassword(view);
            }
        });

        //para que no se abra el teclado al entrar en la activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void savePassword(View view) {
        validatePassword(view);
    }

    private void validatePassword(View view) {
        String oldPassword = ((TextView) findViewById(R.id._change_password_old_password)).getText().toString();
        String newPassword = ((TextView) findViewById(R.id._change_password_new_password)).getText().toString();
        String repitedPassword = ((TextView) findViewById(R.id._change_password_repite_new_password)).getText().toString();

        if(oldPassword.equals("") || newPassword.equals("") || repitedPassword.equals(""))
            Snackbar.make(view, getString(R.string.password_change_error_password_empty), Snackbar.LENGTH_LONG).show();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
