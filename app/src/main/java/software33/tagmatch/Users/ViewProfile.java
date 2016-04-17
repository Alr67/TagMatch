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
    private String idChat = "Not exists";
    private String userName, titleProduct;
    private String imageChat;
    private ChildEventListener mListener;

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
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);

        Firebase.setAndroidContext(this);

        //TODO:Get user and product
        userName = "Usuario0";
        titleProduct = "ProductExample";
        userId = "aec6538a-bde2-4ea8-98bf-6fdc3f95127e";

        //ivUserImage.setImageURI(Uri.parse("http://i.imgur.com/VN6Zhot.jpg"));

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
            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias() + "/photo", this) {
                @Override
                protected void onPostExecute(String url) {
                    Picasso.with(ViewProfile.this).load(url).error(R.drawable.image0).into(ivUserImage);
                }
            }.execute(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mListener = FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(this)).child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                getChat(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
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
        b.putString("UserName", userName);
        b.putString("TitleProduct", titleProduct);

        if (idChat.equals("Not exists")) {
            b.putString("IdChat", createChat());
        }
        else b.putString("IdChat", idChat);
        b.putString("IdUser", userId);
        b.putString("ImageChat", imageChat);
        intent.putExtras(b);

        startActivity(intent);
    }

    private String createChat() {
        Firebase id = FirebaseUtils.getChatsRef().push();

        //TODO: hardcoded userId
        userId = "aec6538a-bde2-4ea8-98bf-6fdc3f95127e";
        FirebaseUtils firebaseUtils = new FirebaseUtils() {};
        String id1 = firebaseUtils.getMyId(this);
        String id2 = userId;

        Map<String, Object> users = new HashMap<>();
        users.put(id1, "My User Name");
        users.put(id2, userName);

        FirebaseUtils.ChatInfo chatInfo = new FirebaseUtils.ChatInfo(titleProduct, users);
        id.child("info").setValue(chatInfo);

        //Set the chats to each user

        Map<String, Object> chats1 = new HashMap<>();
        chats1.put(id.getKey(),"");
        FirebaseUtils.getUsersRef().child(id1).child("chats").updateChildren(chats1);

        setUserImage(id2);

        return id.getKey();
    }

    private void setUserImage(String userId){
        Drawable drawable = ivUserImage.getDrawable();

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable .getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        String encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT);

        Map<String, Object> img = new HashMap<>();
        img.put("img",encodedImage);
        FirebaseUtils.getUsersRef().child(userId).updateChildren(img);

        imageChat = encodedImage;
    }

    private void getChat(final String idChat) {
        //Accessing to the chat with idChat ONCE
        FirebaseUtils.getChatsRef().child(idChat).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                FirebaseUtils.ChatInfo c = snapshot.getValue(FirebaseUtils.ChatInfo.class);
                setButtonXat(c.getIdProduct(), c.getUsers(), idChat);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void setButtonXat(String idProduct, Map<String, Object> users, String idChat) {
        String userName = "";
        for (Object o : users.values()){
            if (!o.toString().equals("My User Name")) {
                userName = o.toString();
            }
        }
        String userId = "";
        for (String s : users.keySet()){
            if (!s.equals(myId)) {
                userId = s;
            }
        }

        if (idProduct.equals(titleProduct) && userId.equals(this.userId)){
            bStartXat.setText("Xatejant");
            this.idChat = idChat;
        }
        else this.idChat = "Not exists";
    }
}
