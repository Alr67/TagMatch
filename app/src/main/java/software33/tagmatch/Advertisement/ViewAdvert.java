package software33.tagmatch.Advertisement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Chat.SingleChatActivity;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImgurImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class ViewAdvert extends AppCompatActivity implements View.OnClickListener {

    private Button chatButton,favouriteButton;
    private ImageView imageType, userImage;
    private TextView title,description,tags,username,valoration;
    private ViewPager mViewPager;
    private CustomPagerAdapterViewAdvert mCustomPagerAdapterViewAdvert;
    private GoogleMap map;
    Advertisement adv;

    private ChildEventListener mListener;
    private String idChat = "Not exists";
    //TODO: hardcoded userId
    private String userId;
    private String imageChat;
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advert);
        myName = Helpers.getActualUser(this).getAlias();
        initComponents();
        Bundle b = getIntent().getExtras();
        if(b != null) {
            Log.i(Constants.DebugTAG,"Bundle not emptu");
            getAdvertisement(b.getInt(Constants.TAG_BUNDLE_IDVIEWADVERTISEMENT));
        }
        else Log.i(Constants.DebugTAG,"Bundle EMPTY");
    }

    public void getAdvertisement(Integer id) {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        Log.i(Constants.DebugTAG,"Vaig a mostrar l'anunci amb id: "+id);
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
        Toast.makeText(this,"NOT IMPLEMENTED YET",Toast.LENGTH_SHORT).show();
    }

    private void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_advert);
        setSupportActionBar(toolbar);

  /*      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); */
        userImage = (ImageView) findViewById(R.id.advert_user_image);
        userImage.setImageDrawable(getDrawable(R.drawable.loading));
        imageType = (ImageView) findViewById(R.id.advert_image_type);
        valoration = (TextView) findViewById(R.id.advert_valoration);
        username = (TextView) findViewById(R.id.advert_name_user);
        tags = (TextView) findViewById(R.id.advert_tags);
        description = (TextView) findViewById(R.id.advert_description);
        title = (TextView) findViewById(R.id.advert_title);
        favouriteButton = (Button) findViewById(R.id.advert_but_favourites);
        favouriteButton.setOnClickListener(this);

        chatButton = (Button) findViewById(R.id.bStartXat);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.advert_map)).getMap();
        prepareImages();
    }

    private void fillComponents(){
        title.setText(adv.getTitle());
        description.setText(adv.getDescription());
        username.setText(adv.getOwner().getAlias());
        valoration.setText(adv.getOwner().getValoration().toString());
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
        Log.i(Constants.DebugTAG,"User name: "+adv.getUser().getAlias());
        tags.setText(tagsLine);
        if(adv.getUser().getCoord()!=null)map.addMarker(new MarkerOptions().position(adv.getUser().getCoord()));
        getAdvertisementImages();
    }

    private void getAdvertisementImages() {
            /*for(Bitmap image : adv.getImages()) {
            mCustomPagerAdapterViewAdvert.addImage(image);
        }*/
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
            Log.i(Constants.DebugTAG,"Vaig a demanar la foto amb id: "+photoId);

                new TagMatchGetAsyncTask(url+photoId.toString(),this) {
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        Log.i(Constants.DebugTAG,"onPostExecute de la imatge JSON: "+jsonObject.toString());
                        if(jsonObject.has("302")) {
                            try {
                                new TagMatchGetImgurImageAsyncTask(jsonObject.getString("302"),getApplicationContext()) {
                                    @Override
                                    protected void onPostExecute(JSONObject jsonObject) {
                                        Log.i(Constants.DebugTAG,"onPostExecute de la imatge JSON: "+jsonObject.toString());
                                        try {
                                        if(jsonObject.has("image")) {
                                            Bitmap image = Helpers.stringToBitMap(jsonObject.getString("image"));
                                            mCustomPagerAdapterViewAdvert.addImage(image);
                                            Log.i(Constants.DebugTAG,"image added");
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(),jsonObject.getString("error"),Toast.LENGTH_SHORT);
                                        }
                                        //  adv = convertJSONToAdvertisement(jsonObject);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.execute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                          //  adv = convertJSONToAdvertisement(jsonObject);
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
                        Log.i("Debug-GetUser",jsonObject.toString());
                        userId = jsonObject.get("firebaseID").toString();
                    }
                } catch (JSONException ignored) {
                    Log.i("DEBUG","error al get user");
                }
            }
        }.execute(jObject);

        new TagMatchGetImageAsyncTask(url, this) {
            @Override
            protected void onPostExecute(String url) {
                Picasso.with(ViewAdvert.this).load(url).error(R.drawable.image0).into(userImage);
                if (url == null){
                    Picasso.with(ViewAdvert.this).load(R.drawable.image0).into(userImage);
                }
                getChats();
            }
        }.execute(jObject);
    }

    private void getChats(){
        FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(this)).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()){
                    if (!adv.getOwner().getAlias().equals(myName))
                        chatButton.setEnabled(true);
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

        chatButton.getLayoutParams().height=params.height/5;
        chatButton.getLayoutParams().width=params.height/5;
        chatButton.setBackground(getDrawable(R.drawable.adver_chat));
        favouriteButton.getLayoutParams().height=params.height/5;
        favouriteButton.getLayoutParams().width=params.height/5;
        favouriteButton.setBackgroundResource(R.drawable.advert_heart);

        imageType.getLayoutParams().height=params.height/5;
        imageType.getLayoutParams().width=params.height/5;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    public void buttonStartXat(View view) {
        Intent intent = new Intent(this, SingleChatActivity.class);
        Bundle b = new Bundle();
        b.putString("UserName", username.getText().toString());
        b.putString("TitleProduct", title.getText().toString());

        if (idChat.equals("Not exists")) {
            b.putString("IdChat", createChat());
        }
        else b.putString("IdChat", idChat);
        b.putString("IdUser", userId);

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

        startActivity(intent);
    }

    private String createChat() {
        Firebase id = FirebaseUtils.getChatsRef().push();

        String id1 = FirebaseUtils.getMyId(this);
        String id2 = userId;

        Map<String, Object> users = new HashMap<>();
        users.put(id1, Helpers.getActualUser(this).getAlias());
        users.put(id2, username.getText().toString());

        FirebaseUtils.ChatInfo chatInfo = new FirebaseUtils.ChatInfo(title.getText().toString(), users);
        id.child("info").setValue(chatInfo);

        //Set the chats to each user

        Map<String, Object> chats1 = new HashMap<>();
        chats1.put(id.getKey(),"");
        FirebaseUtils.getUsersRef().child(id1).child("chats").updateChildren(chats1);

        return id.getKey();
    }

    private void getChat(final String idChat, final long numChats) {
        //Accessing to the chat with idChat ONCE
        FirebaseUtils.getChatsRef().child(idChat).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                FirebaseUtils.ChatInfo c = snapshot.getValue(FirebaseUtils.ChatInfo.class);
                setButtonXat(c.getIdProduct(), c.getUsers(), idChat, numChats);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void setButtonXat(String idProduct, Map<String, Object> users, String idChat, long numChats) {
        String userName = "";
        for (Object o : users.values()){
            if (!o.toString().equals(Helpers.getActualUser(this).getAlias())) {
                userName = o.toString();
            }
        }
        String userId = "";
        for (String s : users.keySet()){
            if (!s.equals(FirebaseUtils.getMyId(this))) {
                userId = s;
            }
        }

        //Restriccions de obrir xat:
        //      No mateix titul de producte ni id de usuari que ho ha publicat
        if (idProduct.equals(title.getText().toString()) && userId.equals(this.userId)){
            chatButton.setText("Xatejant");
            this.idChat = idChat;
            chatButton.setEnabled(true);
        }
        else {
            this.idChat = "Not exists";
            if (numChats == 1 && !this.username.getText().toString().equals(myName)) chatButton.setEnabled(true);
        }

        Log.i("Debug-Chat","idProd " +idProduct);
        Log.i("Debug-Chat","title.getText().toString() " +title.getText().toString());
        Log.i("Debug-Chat","userId " +userId);
        Log.i("Debug-Chat","this.userId " +this.userId);


    }
}
