package software33.tagmatch.Advertisement;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import software33.tagmatch.ServerConnection.TagMatchDeleteAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetBitmapAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class ViewAdvert extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageType, userImage;
    private TextView description,tags,username,location;
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

    private boolean myAdv;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advert);
        myName = Helpers.getActualUser(this).getAlias();
        initComponents();
        setTitle(R.string.loading_title);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (myAdv) getMenuInflater().inflate(R.menu.menu_view_my_adv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
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

        return super.onOptionsItemSelected(item);
    }

    public void getAdvertisement(Integer id) {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        Log.i(Constants.DebugTAG, "Vaig a mostrar l'anunci amb id: " + id);
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
                        setTitle(adv.getTitle());
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_advert);
        setSupportActionBar(toolbar);

        userImage = (ImageView) findViewById(R.id.advert_user_image);
        userImage.setImageDrawable(getDrawable(R.drawable.loading));

        imageType = (ImageView) findViewById(R.id.advert_image_type);
        location = (TextView) findViewById(R.id.advert_location);
        username = (TextView) findViewById(R.id.advert_name_user);
        tags = (TextView) findViewById(R.id.advert_tags);
        description = (TextView) findViewById(R.id.advert_description);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.advert_map)).getMap();
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
                            userId = jsonObject.get("firebaseID").toString();
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
                Picasso.with(ViewAdvert.this).load(url).error(R.drawable.image0).into(userImage);
                if (url == null){
                    Picasso.with(ViewAdvert.this).load(R.drawable.image0).into(userImage);
                }
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
        Log.i("DEBUG XAT","Xddddd plural");
        FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(this)).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    if (!adv.getOwner().getAlias().equals(myName)) {
                        fab.setClickable(true);
                        fab.setImageDrawable(getDrawable(R.drawable.ic_menu_send));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent chat = buttonStartXat(view);
                                startActivity(chat);
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
        b.putString("TitleProduct", getTitle().toString());

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

        return intent;
    }

    private String createChat() {
        Firebase id = FirebaseUtils.getChatsRef().push();

        String id1 = FirebaseUtils.getMyId(this);
        String id2 = userId;

        Map<String, Object> users = new HashMap<>();
        users.put(id1, Helpers.getActualUser(this).getAlias());
        users.put(id2, username.getText().toString());

        FirebaseUtils.ChatInfo chatInfo = new FirebaseUtils.ChatInfo(getTitle().toString(), users);
        id.child("info").setValue(chatInfo);

        //Set the chats to each user

        Map<String, Object> chats1 = new HashMap<>();
        chats1.put(id.getKey(),"");
        FirebaseUtils.getUsersRef().child(id1).child("chats").updateChildren(chats1);

        return id.getKey();
    }

    private void getChat(final String idChat, final long numChats) {
        //Accessing to the chat with idChat ONCE
        Log.i("DEBUG XAT", "Xd");
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
        if (idProduct.equals(getTitle().toString()) && userId.equals(this.userId)){
            this.idChat = idChat;
            fab.setClickable(true);
            //fab.setImageDrawable(getDrawable(R.drawable.existing_chat));
            fab.setImageDrawable(getDrawable(R.drawable.ic_menu_send));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chat = buttonStartXat(view);
                    startActivity(chat);
                }
            });
        }
        else {
            this.idChat = "Not exists";
            if (numChats == 1 && !this.username.getText().toString().equals(myName)){
                fab.setClickable(true);
                fab.setImageDrawable(getDrawable(R.drawable.ic_menu_send));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chat = buttonStartXat(view);
                        startActivity(chat);
                    }
                });
            }
        }

        Log.i("Debug-Chat","idProd " +idProduct);
        Log.i("Debug-Chat","getTitle().toString() " +getTitle().toString());
        Log.i("Debug-Chat","userId " +userId);
        Log.i("Debug-Chat","this.userId " +this.userId);

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
