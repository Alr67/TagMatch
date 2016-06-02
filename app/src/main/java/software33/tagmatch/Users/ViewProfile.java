package software33.tagmatch.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

import java.util.ArrayList;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvLocation, title;
    private ImageView ivUserImage;
    private ListView interests_hash;
    private GoogleMap map;
    private LatLng userPosition;
    private User user;
    private FloatingActionButton fab;
    private boolean otherUser;
    private RatingBar ratingBar;
    private TextView ratingTV;
    private ProgressDialog mDialog;

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
        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        Helpers.setNavHeader(nav_header,getApplicationContext());
        navigationView.addHeaderView(nav_header);

        Bundle extras = getIntent().getExtras();

        user = Helpers.getActualUser(this);

        ratingBar = (RatingBar) findViewById(R.id.profile_rating_bar);
        ratingTV = (TextView) findViewById(R.id.profile_rating_tv);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);

        interests_hash = (ListView) findViewById(R.id.profile_interests);

        Firebase.setAndroidContext(this);

        try {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.profileMap)).getMap();
            if (map!=null)map.getUiSettings().setScrollGesturesEnabled(false);
        }
        catch (Exception ignored){}

        title = (TextView) findViewById(R.id.toolbar_title_view_profile);
        if(getIntent().hasExtra("username")) {
            otherUser = true;
            initOtherUser(extras.getString("username"));
        }
        else {
            otherUser=false;
            initCurrentUser();
        }

    }

    private void initCurrentUser() {
        title.setText(getResources().getString(R.string.vw_1) + user.getAlias() + getResources().getString(R.string.ed_2));
        if (Build.VERSION.SDK_INT < 23) {
            title.setTextAppearance(getApplicationContext(),R.style.normalText);
        } else {
            title.setTextAppearance(R.style.normalText);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                intent.putExtra("userPosition", userPosition);
                intent.putExtra("username",user.getAlias());
                intent.putExtra("city", user.getCity());
                startActivity(intent);
                finish();
            }
        });

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());

            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias(), this) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mDialog = new ProgressDialog(ViewProfile.this);
                    mDialog.setMessage(getString(R.string.loading));
                    mDialog.show();
                }
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
                                map.getUiSettings().setScrollGesturesEnabled(false);
                            }
                            catch (Exception e) {}
                            JSONArray interestsArray = jsonObject.getJSONArray("interests");
                            ArrayList<String> listdata = new ArrayList<String>();
                            if (interestsArray != null) {
                                for (int i=0;i<interestsArray.length();i++){
                                    listdata.add(interestsArray.get(i).toString());
                                }
                            }
                            interests_hash.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, listdata));
                            double ratingAux = jsonObject.getDouble("rating");
                            ratingBar.setRating((float) ratingAux);
                            ratingTV.setText(jsonObject.getString("rating") + "/5.0");
                            mDialog.dismiss();

                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","error al get user");
                    }
                }
            }.execute(jsonObject);

            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias() + "/photo", this) {
                @Override
                protected void onPostExecute(String url) {
                    if (url == null){
                        Picasso.with(ViewProfile.this).load(R.drawable.image0).centerCrop().resize(ivUserImage.getMeasuredWidth(),ivUserImage.getMeasuredHeight()).into(ivUserImage);
                    }
                    else Picasso.with(ViewProfile.this).load(url).error(R.drawable.image0).centerCrop().resize(ivUserImage.getMeasuredWidth(),ivUserImage.getMeasuredHeight()).into(ivUserImage);
                }
            }.execute(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                intent.putExtra("userPosition", userPosition);
                startActivity(intent);
            }
        });
    }

    private void initOtherUser(String username) {

        title.setText(getResources().getString(R.string.vw_1) + username + getResources().getString(R.string.ed_2));
        if (Build.VERSION.SDK_INT < 23) {
            title.setTextAppearance(getApplicationContext(),R.style.normalText);
        } else {
            title.setTextAppearance(R.style.normalText);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_profile);
        fab.setVisibility(View.GONE);

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
                            ArrayList<String> listdata = new ArrayList<String>();
                            if (interestsArray != null) {
                                for (int i=0;i<interestsArray.length();i++){
                                    listdata.add(interestsArray.get(i).toString());
                                }
                            }
                            interests_hash.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, listdata));
                            double ratingAux = jsonObject.getDouble("rating");
                            ratingBar.setRating((float) ratingAux);
                            ratingTV.setText(jsonObject.getString("rating") + "/5.0");
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

        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getIntent().hasExtra("username")) {
                finish();
            }
            else {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (otherUser) getMenuInflater().inflate(R.menu.view_other_user_advs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.view_other_user_advs:
                Intent intent = new Intent(this, MyAdverts.class);
                intent.putExtra("previousActivity", "ViewOtherProfile");
                intent.putExtra("username", getIntent().getExtras().getString("username"));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
