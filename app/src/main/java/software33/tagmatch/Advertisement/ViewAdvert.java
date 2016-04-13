package software33.tagmatch.Advertisement;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import software33.tagmatch.R;
import software33.tagmatch.Utils.NavigationController;

public class ViewAdvert extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {

    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_view_advert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_advert);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        logout = (Button) findViewById(R.id.xat_profile);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"NOT IMPLEMENTED YET",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
