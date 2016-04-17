package software33.tagmatch.Advertisement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.Domain.AdvChange;
import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.AdvSell;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImgurImageAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewAdvert extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {

    private Button chatButton,favouriteButton;
    private ImageView imageType, userImage;
    private TextView title,description,tags,username,valoration;
    private ViewPager mViewPager;
    private CustomPagerAdapterViewAdvert mCustomPagerAdapterViewAdvert;
    private GoogleMap map;
    Advertisement adv;

    public ViewAdvert(){
        adv = new Advertisement();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_view_advert);
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
                        adv = convertJSONToAdvertisement(jsonObject);
                        fillComponents();
                        //String error = jsonObject.get("error").toString();
                }
            }.execute(jObject);
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA");
            e.printStackTrace();
        }
    }

    private Advertisement convertJSONToAdvertisement(JSONObject jsonObject) {
        Log.i(Constants.DebugTAG,"Lets convert to JSON");
        Advertisement advert = new Advertisement();
        String title,description,type,ownerName,category, userPhotoId, city;
        title = description = type = ownerName = category = userPhotoId = city = "";
        Integer id = 0;
        String[] tags = new String[0];
        String[] photoIds = new String[0];
        try {
            if(jsonObject.has("id")) id = jsonObject.getInt("id");
            if(jsonObject.has("type")) type = jsonObject.getString("type");
            if(jsonObject.has("name")) title = jsonObject.getString("name");
            if(jsonObject.has("owner")) ownerName = jsonObject.getString("owner");
            if(jsonObject.has("category")) category = jsonObject.getString("category");
            if(jsonObject.has("description")) description = jsonObject.getString("description");
            if(jsonObject.has("userPhotoId")) userPhotoId = jsonObject.getString("userPhotoId");
            if(jsonObject.has("city")) city = jsonObject.getString("city");
            User owner = new User(ownerName,userPhotoId,city);
            Log.i(Constants.DebugTAG,"User name from owner: " +owner.getAlias());
            if(jsonObject.has("tags")) {
                JSONArray array = jsonObject.getJSONArray("tags");
                tags = new String[array.length()];
                for(int i = 0; i < array.length();++i) {
                    tags[i] = array.getString(i);
                }
            }
            if(jsonObject.has("photoIds")) {
                JSONArray array = jsonObject.getJSONArray("photoIds");
                photoIds = new String[array.length()];
                for(int i = 0; i < array.length();++i) {
                    photoIds[i] = array.getString(i);
                }
            }
            if(Constants.typeServerSELL.contains(type)) {
                Double price = 0.0;
                if(jsonObject.has("price")) price = jsonObject.getDouble("price");
                advert = new AdvSell(id,title,photoIds,description,tags,category,price);
            }
            else if(Constants.typeServerEXCHANGE.contains(type)) {
                String[] tagsWanted = new String[0];
                if(jsonObject.has("wantedTags"))  {
                    JSONArray array = jsonObject.getJSONArray("wantedTags");
                    tagsWanted = new String[array.length()];
                    for(int i = 0; i < array.length();++i) {
                        tagsWanted[i] = array.getString(i);
                    }
                }
                advert = new AdvChange(id,title,photoIds,description,tags,category,tagsWanted);
            }
            else {
                advert = new AdvGift(id,title,photoIds,description,tags,category);
            }
            advert.setOwner(owner);
            return advert;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.DebugTAG,"User name from covnert func: "+advert.getUser().getAlias());
        return advert;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"NOT IMPLEMENTED YET",Toast.LENGTH_SHORT).show();
    }

    private void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_advert);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userImage = (ImageView) findViewById(R.id.advert_user_image);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }

    /** MARK: images */
    public void newImageConverted(Bitmap bitmap) {
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
}
