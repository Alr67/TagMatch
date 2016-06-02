package software33.tagmatch.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.R;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText numberOfAdvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        setTitle(R.string.settings_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
        navigationView.setNavigationItemSelectedListener(this);

        //para que no se abra el teclado al entrar en la activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        numberOfAdvs = (EditText) findViewById(R.id.settings_default_advertisement_number);
        numberOfAdvs.setText(Integer.toString(Helpers.getDefaultAdvertisementNumber(this)));
    }

    public void goToPasswordChange(View view){
        Intent intent = new Intent(this, PasswordChange.class);
        startActivity(intent);
        finish();
    }

    private boolean saveNewDefaultAdvertLimit() {
        if (!numberOfAdvs.getText().toString().equals("")) {
            if (Integer.parseInt(numberOfAdvs.getText().toString()) <= 0) {
                Helpers.showError(getString(R.string.settings_wrong_default_advs_number), this);
                return false;
            } else
                Helpers.setDefaultAdvertisementNumber(this, Integer.parseInt(numberOfAdvs.getText().toString()));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (saveNewDefaultAdvertLimit()) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
