package software33.tagmatch.Users;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Chat.SingleChatActivity;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUserName, tvLocation;
    Button bStartXat;
    private GoogleMap map;
    private String myId, userId;

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
        bStartXat = (Button) findViewById(R.id.bStartXat);

        Firebase.setAndroidContext(this);

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
                            //TODO: Do coords to the map
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

    public void buttonStartXat(View view) {
        //TODO: Get userName and TitleProduct
        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", "userName");
        b.putString("TitleProduct", "titleProduct");

        b.putString("IdChat", createChat());
        b.putString("IdUser", "aec6538a-bde2-4ea8-98bf-6fdc3f95127e");
        intent.putExtras(b);

        startActivity(intent);
    }

    public String createChat() {
        Firebase id = FirebaseUtils.getUsersRef().push();

        userId = "aec6538a-bde2-4ea8-98bf-6fdc3f95127e";
        FirebaseUtils firebaseUtils = new FirebaseUtils() {};
        String id1 = firebaseUtils.getMyId();
        String id2 = userId;

        Map<String, Object> users = new HashMap<>();
        users.put(id1, "My User Name");
        users.put(id2, "Usuari0");

        FirebaseUtils.ChatInfo chatInfo = new FirebaseUtils.ChatInfo("Anuncio Test", users);
        id.child("info").setValue(chatInfo);

        //Set the chats to each user
        Map<String, Object> chats1 = new HashMap<>();
        chats1.put(id.getKey(),"");
        FirebaseUtils.getUsersRef().child(id1).child("chats").setValue(chats1);

        /*
        Map<String, Object> chats2 = new HashMap<>();
        chats2.put(id.getKey(),"");
        usersRef.child(id2).child("chats").setValue(chats2);
        */

        return id.getKey();
    }
}
