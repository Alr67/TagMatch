package software33.tagmatch.Users;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUserName, tvLocation;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_profile);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvLocation = (TextView) findViewById(R.id.tvLocation);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.profileMap)).getMap();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());
            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users", this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            showError(error);
                        }
                        else if (jsonObject.has("username")){
                            tvUserName.setText(jsonObject.get("username").toString());
                            tvLocation.setText(jsonObject.get("email").toString());
                            CameraUpdate center=
                                    CameraUpdateFactory.newLatLng(new LatLng(40.76793169992044,
                                            -73.98180484771729));
                            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                            map.moveCamera(center);
                            map.animateCamera(zoom);
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","XD");
                    }
                }
            }.execute(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.i("DEBUG","Drawer is open");
        } else {
            Log.i("DEBUG","Drawer is NOT open");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }

    private void showError (String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }
}
