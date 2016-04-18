package software33.tagmatch.Advertisement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advert);
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
        chatButton = (Button) findViewById(R.id.advert_but_chat);
        chatButton.setOnClickListener(this);
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
                                            Bitmap image = stringToBitMap(jsonObject.getString("image"));
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
        getUserImage();
    }

    private Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void getUserImage() {
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

        Log.i(Constants.DebugTAG,"Let's get User image amb url "+url);
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    Log.i(Constants.DebugTAG,"onPostExecute de la imatge de l'user JSON: "+jsonObject.toString());
                    if(jsonObject.has("302")) {
                        try {
                            new TagMatchGetImgurImageAsyncTask(jsonObject.getString("302"),getApplicationContext()) {
                                @Override
                                protected void onPostExecute(JSONObject jsonObject) {
                                    Log.i(Constants.DebugTAG,"onPostExecute de la imatge de l'user JSON: "+jsonObject.toString());
                                    try {
                                        if(jsonObject.has("image")) {
                                            Bitmap image = stringToBitMap(jsonObject.getString("image"));
                                            userImage.setImageBitmap(image);
                                            Log.i(Constants.DebugTAG,"USER image added");
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

    public void prepareImages() {
        mViewPager = (ViewPager) findViewById(R.id.pager_view_advert);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceHeight = displayMetrics.heightPixels;
// Changes the height and width to the specified *pixels*
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = deviceHeight/3;
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
}
