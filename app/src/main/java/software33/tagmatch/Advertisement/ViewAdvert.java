package software33.tagmatch.Advertisement;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software33.tagmatch.AdCards.AdvertContent;
import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Chat.SingleChatActivity;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchDeleteAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetBitmapAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Users.ViewProfile;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class ViewAdvert extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageType, userImage;
    private TextView description,tags,username,location, title;
    private ViewPager mViewPager;
    private CustomPagerAdapterViewAdvert mCustomPagerAdapterViewAdvert;
    private GoogleMap map;
    private Advertisement adv;

    private ChildEventListener mListener;
    private String idChat = "Not exists";
    //TODO: hardcoded userId
    private String userId;
    private String imageChat;
    private String myName;

    private String idProduct;

    private Menu my_menu;


    private boolean myAdv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advert);
        myName = Helpers.getActualUser(this).getAlias();
        initComponents();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_advert);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setClickable(false);
        fab.setImageDrawable(getDrawable(R.drawable.loading));
        Bundle b = getIntent().getExtras();
        if(b != null) {
            Log.i(Constants.DebugTAG,"Bundle not emptu");
            if (b.getString(Constants.TAG_BUNDLE_USERVIEWADVERTISEMENT) != null && b.getString(Constants.TAG_BUNDLE_USERVIEWADVERTISEMENT).equals(Helpers.getActualUser(this).getAlias())){
                myAdv = true;
            }
            else myAdv = false;

            getAdvertisement(b.getInt(Constants.TAG_BUNDLE_IDVIEWADVERTISEMENT));
        }
        else Log.i(Constants.DebugTAG,"Bundle EMPTY");


    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        my_menu = menu;
        if (myAdv) getMenuInflater().inflate(R.menu.menu_view_my_adv, menu);
        else {
            JSONObject jObject = new JSONObject();
            User actualUser = Helpers.getActualUser(this);
            String url = Constants.IP_SERVER + "/favs";
            try {
                jObject.put("username", actualUser.getAlias());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jObject.put("password", actualUser.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new TagMatchGetAsyncTask(url, this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if (jsonObject.has("status"))
                            Log.i(Constants.DebugTAG, "status: " + jsonObject.getInt("status"));
                        Log.i(Constants.DebugTAG, "JSON: \n" + jsonObject);
                        if (jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                        } else {
                            Log.i(Constants.DebugTAG, "PUTITA PARA DENTRO");
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = jsonObject.getJSONArray("arrayResponse");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            boolean stop = false;
                            for (int n = 0; n < jsonArray.length() && !stop; n++) {
                                JSONObject object = null;
                                try {
                                    object = jsonArray.getJSONObject(n);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Advertisement newAdvert = Helpers.convertJSONToAdvertisement(object);
                                if (newAdvert.getID().equals(adv.getID())) {
                                    getMenuInflater().inflate(R.menu.menu_view_foreign_unfav_adv, menu);
                                    stop = true;
                                }
                            }
                            if (!stop){
                                getMenuInflater().inflate(R.menu.menu_view_foreign_adv, menu);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(jObject);
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if(id == R.id.action_report_adv) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Report");

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reportAdv(input.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else if (id == R.id.action_delete) {
            AlertDialog alertDialog = createDeleteDialog();
            alertDialog.show();
        }
        else if (id == R.id.tagmatch_exchange) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.putExtra("previousActivity", "ex_tagmatch");
            String url = Constants.IP_SERVER + "/ads/" + adv.getID() + "/tagmatch";
            intent.putExtra("url", url);
            startActivity(intent);
            //finish();
        }
        else if (id == R.id.action_fav) {
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users/"+ adv.getID().toString() + "/fav", getApplicationContext()){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if (jsonObject.has("status"))  Log.i(Constants.DebugTAG,"status: "+jsonObject.getInt("status"));
                        Log.i(Constants.DebugTAG,"JSON: \n"+jsonObject);
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                        }
                        else {
                            if(jsonObject.has("id")) {
                                Log.i(Constants.DebugTAG,"Advert data updated to server, proceed to update image");
                                Toast.makeText(getApplicationContext(),R.string.added_fav,Toast.LENGTH_LONG).show();
                                invalidateOptionsMenu();
                                getMenuInflater().inflate(R.menu.menu_view_foreign_unfav_adv, my_menu);
                            }
                        }
                    } catch (JSONException ignored){

                    }
                }
            }.execute(adv.toJSON2());
            Log.i(Constants.DebugTAG, adv.toJSON().toString());
        }
        else if (id == R.id.action_unfav) {
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users/"+ adv.getID() + "/unfav", getApplicationContext()){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if (jsonObject.has("status"))  Log.i(Constants.DebugTAG,"status: "+jsonObject.getInt("status"));
                        Log.i(Constants.DebugTAG,"JSON: \n"+jsonObject);
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                        }
                        else {
                            if(jsonObject.has("id")) {
                                Log.i(Constants.DebugTAG,"Advert data updated to server, proceed to update image");
                                Toast.makeText(getApplicationContext(),R.string.erased_fav,Toast.LENGTH_LONG).show();
                                invalidateOptionsMenu();
                                getMenuInflater().inflate(R.menu.menu_view_foreign_adv, my_menu);
                            }
                        }
                    } catch (JSONException ignored){

                    }
                }
            }.execute(adv.toJSON2());
            Log.i(Constants.DebugTAG, adv.toJSON().toString());
        }

        return super.onOptionsItemSelected(item);
    }

    private void reportAdv(String cause) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cause", cause);
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/ads/" + adv.getID() + "/denounce", this){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error, getApplicationContext());
                    } catch (JSONException ignored) {}
                }
            }.execute(jsonObject);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Report send.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception ignored){}
    }

    public void getAdvertisement(Integer id) {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        Log.i(Constants.DebugTAG, "Vaig a mostrar l'anunci amb id: " + id);
        idProduct = id.toString();
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            Log.i(Constants.DebugTAG,"Vaig a fer el get a: "+ Constants.IP_SERVER+"/ads/"+id.toString());
            new TagMatchGetAsyncTask(Constants.IP_SERVER+"/ads/"+id.toString(),this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    Log.i(Constants.DebugTAG,"onPostExecute");
                        Log.i(Constants.DebugTAG,"JSON: "+jsonObject.toString());
                        adv = Helpers.convertJSONToAdvertisement(jsonObject);
                        Log.i(Constants.DebugTAG, "Vaig a mostrar l'anunci amb titol: " + adv.getTitle());
                        title.setText(adv.getTitle());
                        fillComponents();
                        //String error = jsonObject.get("error").toString();
                }
            }.execute(jObject);
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this,R.string.wait_chat,Toast.LENGTH_SHORT).show();
    }

    private void initComponents() {
        userImage = (ImageView) findViewById(R.id.advert_user_image);
        userImage.setImageDrawable(getDrawable(R.drawable.loading));

        title = (TextView) findViewById(R.id.toolbar_title_view_ad);
        title.setText(R.string.loading_title);
        if (Build.VERSION.SDK_INT < 23) {
            title.setTextAppearance(getApplicationContext(),R.style.normalText);
        } else {
            title.setTextAppearance(R.style.normalText);
        }

        imageType = (ImageView) findViewById(R.id.advert_image_type);
        location = (TextView) findViewById(R.id.advert_location);
        username = (TextView) findViewById(R.id.advert_name_user);
        tags = (TextView) findViewById(R.id.advert_tags);
        description = (TextView) findViewById(R.id.advert_description);

        try {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.advert_map)).getMap();
            map.getUiSettings().setScrollGesturesEnabled(false);
        }
        catch (Exception ignored){}

        prepareImages();
    }

    private void fillComponents(){
        description.setText(adv.getDescription());
        username.setText(adv.getOwner().getAlias());
        location.setText(adv.getUser().getCity());
        if(adv.getTypeDescription().equals(Constants.typeServerEXCHANGE)) {
            imageType.setImageDrawable(getDrawable(R.drawable.advert_exchange));
        }
        else if(adv.getTypeDescription().equals(Constants.typeServerSELL)) {
            imageType.setImageDrawable(getDrawable(R.drawable.advert_sell));
        }
        else {
            imageType.setImageDrawable(getDrawable(R.drawable.advert_gift));
        }
        String tagsLine = "";
        String[] advTags = adv.getTags();
        for(int i = 0; i < adv.getTags().length;++i) {
            tagsLine = tagsLine + "#" +advTags[i] + " ";
        }
        Log.i(Constants.DebugTAG,"TAGS: "+tagsLine);
        Log.i(Constants.DebugTAG, "User name: " + adv.getUser().getAlias());
        tags.setText(tagsLine);
        if(adv.getUser().getCoord()!=null)map.addMarker(new MarkerOptions().position(adv.getUser().getCoord()));
        getAdvertisementImages();
    }

    private void getAdvertisementImages() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        try {
        jObject.put("username", actualUser.getAlias());
        jObject.put("password", actualUser.getPassword());
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA amb Json");
            e.printStackTrace();
        }
        String url = Constants.IP_SERVER+"/ads/"+ adv.getID().toString()+"/photo/";
        Log.i(Constants.DebugTAG,"Aquest anunci te "+adv.getImagesIDs().length+" fotos");
        for (String photoId :adv.getImagesIDs()) {
            Log.i(Constants.DebugTAG, "Vaig a demanar la foto amb id: " + photoId);

                new TagMatchGetBitmapAsyncTask(url+photoId.toString(),getApplicationContext()) {
                    @Override
                    protected void onPostExecute(Bitmap image) {
                            mCustomPagerAdapterViewAdvert.addImage(image);
                            Log.i(Constants.DebugTAG,"image added");
                    }
                }.execute(jObject);
        }
        getUserImageAndFirebaseID();
    }

    private void getUserImageAndFirebaseID() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA amb Json");
            e.printStackTrace();
        }
        String url = Constants.IP_SERVER+"/users/"+ adv.getUser().getAlias()+"/photo";

        new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users/" + adv.getUser().getAlias(), this) {
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    if(jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error,getApplicationContext());
                    }
                    else if (jsonObject.has("username")){
                        try {
                            Log.i("Debug-GetUser", jsonObject.toString());
                            userId = adv.getUser().getAlias();
                            location.setText(jsonObject.get("city").toString());
                            LatLng latLng = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                            CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                            map.addMarker(new MarkerOptions().position(latLng));
                        }catch(Exception e){}
                    }
                } catch (JSONException ignored) {
                    Log.i("DEBUG","error al get user");
                }
            }
        }.execute(jObject);

        new TagMatchGetImageAsyncTask(url, this) {
            @Override
            protected void onPostExecute(String url) {
                if (url == null){
                    Picasso.with(ViewAdvert.this).load(R.drawable.image0).into(userImage);
                }
                else Picasso.with(ViewAdvert.this).load(url).error(R.drawable.image0).into(userImage);
                getChats();
                if(myAdv)prepareEdit();
            }
        }.execute(jObject);
    }

    private void prepareEdit() {
        fab.setClickable(true);
        fab.setImageDrawable(getDrawable(R.drawable.ic_menu_manage));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent novaRecepta = new Intent(getApplicationContext(), NewAdvertisement.class);
                novaRecepta.putExtra("edit", true);
                novaRecepta.putExtra("idAnunci",adv.getID());
                startActivity(novaRecepta);
                finish();
            }
        });
    }

    private void getChats(){
        FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(this)).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    if (!adv.getOwner().getAlias().equals(myName)) {
                        fab.setClickable(true);
                        fab.setImageDrawable(getDrawable(R.drawable.chat_add));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent chat = buttonStartXat(view);
                                startActivity(chat);
                                finish();
                            }
                        });
                    }
                }
                else {
                    long numChats = dataSnapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                        getChat(dataSnapshot1.getKey(), numChats);
                        --numChats;
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void prepareImages() {
        mViewPager = (ViewPager) findViewById(R.id.pager_view_advert);

// Changes the height and width to the specified *pixels*
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = Helpers.getDisplayHeight(this)/3;
        mViewPager.setLayoutParams(params);
        mCustomPagerAdapterViewAdvert = new CustomPagerAdapterViewAdvert(this,params.height,params.width);
        mViewPager.setAdapter(mCustomPagerAdapterViewAdvert);

        imageType.getLayoutParams().height=params.height/5;
        imageType.getLayoutParams().width=params.height/5;
    }

    public void goToUser(View view) {
        if(!adv.getOwner().getAlias().equals(Helpers.getActualUser(getApplicationContext()).getAlias())) {
            Intent intent = new Intent(this, ViewProfile.class);
            intent.putExtra("username", adv.getOwner().getAlias());
            startActivity(intent);
            //finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    public Intent buttonStartXat(View view) {
        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", username.getText().toString());
        b.putString("TitleProduct", title.getText().toString());

        if (idChat.equals("Not exists")) {
            b.putString("IdChat", createChat());
        }
        else b.putString("IdChat", idChat);
        b.putString("IdUser", userId);

        b.putBoolean("FromAdvert",true);

        //Get user Image
        Drawable drawable = userImage.getDrawable();

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable .getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        String encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT);

        imageChat = encodedImage;

        FirebaseUtils.setChatImage(imageChat,this);
        intent.putExtras(b);

        return intent;
    }

    private String createChat() {
        Firebase id = FirebaseUtils.getChatsRef().push();

        String id1 = FirebaseUtils.getMyId(this);
        String id2 = userId;

        Map<String, Object> users = new HashMap<>();
        users.put(id1, Helpers.getActualUser(this).getAlias());
        users.put(id2, username.getText().toString());

        FirebaseUtils.ChatInfo chatInfo = new FirebaseUtils.ChatInfo(idProduct, title.getText().toString(), id2, users);
        id.child("info").setValue(chatInfo);

        //Set the chats to each user

        Map<String, Object> chats1 = new HashMap<>();
        chats1.put(id.getKey(),true);
        FirebaseUtils.getUsersRef().child(id1).child("chats").updateChildren(chats1);

        return id.getKey();
    }

    private void getChat(final String idChat, final long numChats) {
        //Accessing to the chat with idChat ONCE
        FirebaseUtils.getChatsRef().child(idChat).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                FirebaseUtils.ChatInfo c = snapshot.getValue(FirebaseUtils.ChatInfo.class);
                setButtonXat(c.getIdProduct(), idChat, numChats);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void setButtonXat(String idProduct, String idChat, long numChats) {

        //Restriccions de obrir xat:
        if (idProduct.equals(this.idProduct)){
            this.idChat = idChat;
            fab.setClickable(true);
            fab.setImageDrawable(getDrawable(R.drawable.ic_menu_send));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chat = buttonStartXat(view);
                    startActivity(chat);
                    finish();
                }
            });
        }
        else {
            this.idChat = "Not exists";
            if (numChats == 1 && !this.username.getText().toString().equals(myName)){
                fab.setClickable(true);
                fab.setImageDrawable(getDrawable(R.drawable.chat_add));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chat = buttonStartXat(view);
                        startActivity(chat);
                        finish();
                    }
                });
            }
        }
    }

    public AlertDialog createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_title_delete_adv)
                .setMessage(R.string.dialog_message_delete_adv)
                .setPositiveButton(R.string.dialog_confirm_delete_adv,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAdvertisement();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel_delete_adv,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //onNegativeButtonClick();
                            }
                        });

        return builder.create();
    }

    private void deleteAdvertisement() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA amb Json");
            e.printStackTrace();
        }

        String url = Constants.IP_SERVER+"/ads/"+ adv.getID();

        new TagMatchDeleteAsyncTask(url, this) {
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    if(jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error,getApplicationContext());
                        Log.i("DEBUGASSOJODERXD",jsonObject.toString());
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException ignored) {
                    Log.i("DEBUG","error al get user");
                }
            }
        }.execute(jObject);
    }
}
