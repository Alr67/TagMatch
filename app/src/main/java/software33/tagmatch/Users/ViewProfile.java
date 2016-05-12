package software33.tagmatch.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUserName, tvLocation;
    ImageView ivUserImage;
    private GoogleMap map;
    LatLng userPosition;


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

        Bundle extras = getIntent().getExtras();

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);

        Firebase.setAndroidContext(this);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.profileMap)).getMap();

        try{
            initOtherUser(extras.getString("username"));
        } catch (Exception e){
            initCurrentUser();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                intent.putExtra("userPosition", userPosition);
                startActivity(intent);
            }
        });
    }

    private void initCurrentUser() {
        User user = Helpers.getActualUser(this);
        tvUserName.setText(user.getAlias());

        final TextView interests = (TextView) findViewById(R.id.profile_interests);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());

            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias(), this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error,getApplicationContext());
                        }
                        else if (jsonObject.has("username")){
                            Log.i("Debug-GetUser",jsonObject.toString());
                            tvLocation.setText(jsonObject.get("city").toString());

                            userPosition = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                            try {
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                                map.addMarker(new MarkerOptions().position(userPosition));
                            }
                            catch (Exception e) {}
                            JSONArray interestsArray = jsonObject.getJSONArray("interests");
                            String interestsString = "";
                            for(int i = 0; i < interestsArray.length(); ++i) {
                                if(i != 0)
                                    interestsString += " ";
                                interestsString += "#"+interestsArray.getString(i);
                            }
                            interests.setText(interestsString);
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","error al get user");
                    }
                }
            }.execute(jsonObject);

            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias() + "/photo", this) {
                @Override
                protected void onPostExecute(String url) {
                    Picasso.with(ViewProfile.this).load(url).error(R.drawable.image0).into(ivUserImage);
                    if (url == null){
                        Picasso.with(ViewProfile.this).load(R.drawable.image0).into(ivUserImage);
                    }
                }
            }.execute(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initOtherUser(String username) {
        tvUserName.setText(username);

        final TextView interests = (TextView) findViewById(R.id.profile_interests);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());

            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users/" + username, this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error,getApplicationContext());
                        }
                        else if (jsonObject.has("username")){
                            Log.i("Debug-GetUser",jsonObject.toString());
                            tvLocation.setText(jsonObject.get("city").toString());

                            userPosition = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                            try {
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                                map.addMarker(new MarkerOptions().position(userPosition));
                            }
                            catch (Exception e) {}
                            JSONArray interestsArray = jsonObject.getJSONArray("interests");
                            String interestsString = "";
                            for(int i = 0; i < interestsArray.length(); ++i) {
                                if(i != 0)
                                    interestsString += " ";
                                interestsString += "#"+interestsArray.getString(i);
                            }
                            interests.setText(interestsString);
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","error al get user");
                    }
                }
            }.execute(jsonObject);

            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + username + "/photo", this) {
                @Override
                protected void onPostExecute(String url) {
                    Picasso.with(ViewProfile.this).load(url).error(R.drawable.image0).into(ivUserImage);
                    if (url == null){
                        Picasso.with(ViewProfile.this).load(R.drawable.image0).into(ivUserImage);
                    }
                }
            }.execute(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
