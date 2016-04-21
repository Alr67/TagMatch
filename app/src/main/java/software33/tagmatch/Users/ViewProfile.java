package software33.tagmatch.Users;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Chat.SingleChatActivity;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUserName, tvLocation;
    ImageView ivUserImage;
    Button bStartXat;
    private GoogleMap map;
    private String myId, userId;
    private String userName, titleProduct;
    private String imageChat;

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
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);

        Firebase.setAndroidContext(this);

        //TODO:Get user and product
        userName = "Usuario0";
        titleProduct = "ProductExample";
        userId = "aec6538a-bde2-4ea8-98bf-6fdc3f95127e";

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.profileMap)).getMap();

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
                            showError(error);
                        }
                        else if (jsonObject.has("username")){
                            tvUserName.setText(jsonObject.get("username").toString());
                            tvLocation.setText(jsonObject.get("city").toString());

                            LatLng latLng = new LatLng(jsonObject.getInt("latitude"),jsonObject.getInt("longitude"));
                            CameraUpdate center=
                                    CameraUpdateFactory.newLatLng(latLng);
                            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                            map.moveCamera(center);
                            map.animateCamera(zoom);
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","XD");
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
