package software33.tagmatch.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software33.tagmatch.AdCards.AdapterAdvert;
import software33.tagmatch.AdCards.AdvertContent;
import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Advertisement.NewAdvertisement;
import software33.tagmatch.Advertisement.ViewAdvert;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class MyAdverts extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    private AdapterAdvert adapter;
    private RecyclerView.LayoutManager lManager;
    private List<Advertisement> advertisements;
    private ArrayList<AdvertContent> items;
    private TextView loading;
    private boolean otherUser;
    private String username;
    private TextView title;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_my_adverts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_my_adverts);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_my_adverts);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        Helpers.setNavHeader(nav_header,getApplicationContext(),this);
        navigationView.addHeaderView(nav_header);

        String title = getString(R.string.vw_1);

        if(getIntent().hasExtra("previousActivity") && getIntent().getExtras().getString("previousActivity").equals("ViewOtherProfile")){
            otherUser = true;
            username = getIntent().getExtras().getString("username");
            title += username;
        }
        else {
            otherUser = false;
            title += Helpers.getActualUser(this).getAlias();
        }

        title += getString(R.string.my_advs_title_end);
        setTitle(title);

        initComponents();
    }

    private void initComponents() {
   /*     loading = (TextView) findViewById(R.id.text_loading);
        ViewGroup.LayoutParams params = loading.getLayoutParams();
        params.height = Helpers.getDisplayHeight(this)/2;
        loading.setGravity(Gravity.BOTTOM|Gravity.FILL_VERTICAL);
        loading.setLayoutParams(params);
*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_my_adverts);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent novaRecepta = new Intent(getApplicationContext(), NewAdvertisement.class);
                startActivity(novaRecepta);
                finish();
            }
        });

        if(otherUser)
            fab.setVisibility(View.GONE);

        items = new ArrayList<>();

        downloadAdvertsFromServer();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        recycler = (RecyclerView) findViewById(R.id.reciclador);//Creamos el recycler
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);//El manager
        recycler.setLayoutManager(lManager);

        adapter = new AdapterAdvert(items, getApplicationContext());// Y el adaptador

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override  //recView.getChildPosition(v)
            public void onClick(View v) {
                Integer id = items.get(v.getTag().hashCode()).getAd_id();
                Intent viewRecepta = new Intent(getApplicationContext(), ViewAdvert.class).putExtra(Constants.TAG_BUNDLE_IDVIEWADVERTISEMENT, id);
                viewRecepta.putExtra(Constants.TAG_BUNDLE_USERVIEWADVERTISEMENT,Helpers.getActualUser(getApplicationContext()).getAlias());
                startActivity(viewRecepta);
                finish();
            }
        });
        recycler.setAdapter(adapter);
    }

    //TODO: POSAR ANUNCIS DEL USER
    private void downloadAdvertsFromServer() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        String url;
        if(otherUser)
            url = Constants.IP_SERVER+"/users/"+username+"/ads?limit="+Constants.SERVER_limitAdverts;
        else
            url = Constants.IP_SERVER+"/users/"+actualUser.getAlias()+"/ads?limit="+Constants.SERVER_limitAdverts;
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            Log.i(Constants.DebugTAG,"Vaig a demanar un get a la url: "+url);
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mDialog = new ProgressDialog(MyAdverts.this);
                    mDialog.setMessage(getString(R.string.loading));
                    mDialog.show();
                }
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    if(jsonObject.has("arrayResponse")) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("arrayResponse");
                            advertisements = new ArrayList<>();
                            if(jsonArray.length()>0) {
                              //  hideLoading();
                                for (int n = 0; n < jsonArray.length(); n++) {
                                    JSONObject object = jsonArray.getJSONObject(n);
                                    Advertisement newAdvert = Helpers.convertJSONToAdvertisement(object);
                                    advertisements.add(newAdvert);
                                    String imageId;
                                    if (newAdvert.getImagesIDs().length > 0)
                                        imageId = newAdvert.getImagesIDs()[0];
                                    else imageId = "";

                                    //Code to get the sold
                                    boolean sold = false;
                                    if(object.has("sold")) sold = object.getBoolean("sold");

                                    items.add( new AdvertContent(newAdvert.getTitle(),imageId, newAdvert.getTypeDescription(), newAdvert.getPrice(), newAdvert.getOwner().getAlias(), newAdvert.getID(), sold));
                                }
                                adapter.notifyDataSetChanged();
                                mDialog.dismiss();
                            }
                            else {
                                showNotAdvertsMessage();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotAdvertsMessage() {
        loading = (TextView) findViewById(R.id.text_loading);
        ViewGroup.LayoutParams params = loading.getLayoutParams();
        params.height = Helpers.getDisplayHeight(this)/2;
        loading.setGravity(Gravity.BOTTOM|Gravity.FILL_VERTICAL);
        loading.setLayoutParams(params);
        loading.setText(getString(R.string.hint_no_adverts));
        mDialog.dismiss();
    }

    /*   private void hideLoading() {
           ViewGroup.LayoutParams params = loading.getLayoutParams();
           params.height = 0;
           loading.setLayoutParams(params);
       }
   */
    @Override
    public void onClick(View v) {
        Toast.makeText(this,"NOT IMPLEMENTED YET",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, MyAdverts.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
