package software33.tagmatch.Advertisement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

public class DiscoveryTagmatch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    private AdapterAdvert adapter;
    private RecyclerView.LayoutManager lManager;
    private List<Advertisement> advertisements;
    private ArrayList<AdvertContent> items;
    private TextView loading;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //Esconder titulo HAMBURGUER
        TextView app_header = (TextView) toolbar.findViewById(R.id.toolbar_title_disc); //cogemos el textview de la toolbar
        app_header.setText(R.string.title_discovery);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-BoldItalic.ttf");//aplicamos el diseño
        app_header.setTypeface(face);

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

        initComponents();

    }

    private void initComponents() {


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
                viewRecepta.putExtra(Constants.TAG_BUNDLE_USERVIEWADVERTISEMENT, Helpers.getActualUser(getApplicationContext()).getAlias());
                startActivity(viewRecepta);
                finish();
            }
        });
        recycler.setAdapter(adapter);
    }

    private void downloadAdvertsFromServer() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        String url = Constants.IP_SERVER + "/users/discovery";
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            Log.i(Constants.DebugTAG,"Vaig a demanar un get a la url: "+url);
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mDialog = new ProgressDialog(DiscoveryTagmatch.this);
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
                                    items.add( new AdvertContent(newAdvert.getTitle(),imageId, newAdvert.getTypeDescription(), newAdvert.getPrice(), newAdvert.getOwner().getAlias(), newAdvert.getID()));
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
        loading = (TextView) findViewById(R.id.text_loading_disc);
        ViewGroup.LayoutParams params = loading.getLayoutParams();
        params.height = Helpers.getDisplayHeight(this)/2;
        loading.setGravity(Gravity.BOTTOM|Gravity.FILL_VERTICAL);
        loading.setLayoutParams(params);
        loading.setText(getString(R.string.hint_no_hash));
        mDialog.dismiss();
    }

    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }
}
