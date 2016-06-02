package software33.tagmatch.AdCards;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import software33.tagmatch.Advertisement.ViewAdvert;
import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.Filter.Filter;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    private AdapterAdvert adapter;
    private RecyclerView.LayoutManager lManager;
    private List<Advertisement> advertisements;
    private ArrayList<AdvertContent> items;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pg_principal);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // COMPROBAR SI HAY NUMERO DE ANUNCIOS POR DEFECTO
        // Y PONER UN NUMERO EN CASO NEGATIVO
        if(Helpers.getDefaultAdvertisementNumber(this) == -1)
            Helpers.setDefaultAdvertisementNumber(this, 40);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //Esconder titulo HAMBURGUER
        TextView app_header = (TextView) toolbar.findViewById(R.id.toolbar_title); //cogemos el textview de la toolbar
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-BoldItalic.ttf");//aplicamos el diseño
        app_header.setTypeface(face);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        Helpers.setNavHeader(nav_header,getApplicationContext(),this);
        navigationView.addHeaderView(nav_header);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent novaRecepta = new Intent(getApplicationContext(), Filter.class);
                startActivity(novaRecepta);
                finish();
            }
        });

        items = new ArrayList<>();

        downloadAdvertsFromServer(getIntent().getExtras());

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
                viewRecepta.putExtra(Constants.TAG_BUNDLE_USERVIEWADVERTISEMENT,items.get(v.getTag().hashCode()).getOwner());
                startActivity(viewRecepta);
                finish();
            }
        });
        recycler.setAdapter(adapter);

        startChatListeners();
    }

    private void downloadAdvertsFromServer(Bundle extras) {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        String url = Constants.IP_SERVER;
        try{
            if(extras.getString("previousActivity").equals("filter") || extras.getString("previousActivity").equals("ex_tagmatch") ) {
                url = extras.getString("url");
            }
        } catch (Exception e){
            url += "/ads?idGreaterThan=" + Constants.SERVER_IdGreaterThan + "&limit=" + Helpers.getDefaultAdvertisementNumber(this);
        }
        Log.i("url", url);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            new TagMatchGetAsyncTask(url,this) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mDialog = new ProgressDialog(Home.this);
                    mDialog.setMessage(getString(R.string.loading));
                    mDialog.show();
                }
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    if(jsonObject.has("arrayResponse")) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("arrayResponse");
                            advertisements = new ArrayList<>();
                            for (int n = 0; n < jsonArray.length(); n++) {
                                JSONObject object = jsonArray.getJSONObject(n);
                                Advertisement newAdvert = Helpers.convertJSONToAdvertisement(object);
                                advertisements.add(newAdvert);
                                String imageId;
                                if(newAdvert.getImagesIDs().length>0) imageId = newAdvert.getImagesIDs()[0];
                                else imageId = "";
                                items.add( new AdvertContent(newAdvert.getTitle(),imageId, newAdvert.getTypeDescription(), newAdvert.getPrice(), newAdvert.getOwner().getAlias(), newAdvert.getID()));
                            }
                            adapter.notifyDataSetChanged();
                            mDialog.dismiss();

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

    private void startChatListeners(){
        FirebaseUtils.startListeners(FirebaseUtils.getMyId(this), this);
        //FirebaseUtils.startService(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return NavigationController.onItemSelected(item.getItemId(),this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }
}
