package software33.tagmatch.AdCards;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import software33.tagmatch.Login_Register.Login;
import software33.tagmatch.R;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.NavigationController;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    private AdapterAdvert adapter;
    private RecyclerView.LayoutManager lManager;

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

        getSupportActionBar().setDisplayShowTitleEnabled(false); //Esconder titulo HAMBURGUER
        TextView app_header = (TextView) toolbar.findViewById(R.id.toolbar_title); //cogemos el textview de la toolbar
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-BoldItalic.ttf");//aplicamos el diseño
        app_header.setTypeface(face);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent novaRecepta = new Intent(getApplicationContext(), NovaRecepta.class);
                startActivity(novaRecepta);
                finish();*/
            }
        });



        //final ArrayList<Receptes> items = new DBController().loadLlistaReceptes(this);
        final ArrayList<AdvertContent> items = new ArrayList<>();

        /*for(int q = 0; q < items.size(); ++q) { FOR PARA TRATAR COSAS QUE NOS LLEGARAN DEL SERVER. TENDREMOS QUE PILLAR NAME Y EL PATH DEL IMGUR
            items.add(new CardCreator(getApplicationContext(),items.get(q)));
        }*/
                                                                            //0-> GiveAway 1-> Exchange 2-> Sell
        items.add(new CardCreator().createCard(getApplicationContext(),"Espaguetis 2 Caca A ver que Ostia Que Esto Ha Hecho Algo Muy Guay","espaguetis.jpg", Constants.card_exchange ,0)); // primer cero es opcion, el segundo precio
        items.add(new CardCreator().createCard(getApplicationContext(),"Espaguetis 2 Caca A ver que Ostia Que Esto Ha Hecho Algo Muy Guay","espaguetis.jpg", Constants.card_sell , 33));

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        recycler = (RecyclerView) findViewById(R.id.reciclador);//Creamos el recycler
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);//El manager
        recycler.setLayoutManager(lManager);

        adapter = new AdapterAdvert(items, getApplicationContext());// Y el adaptador

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override  //recView.getChildPosition(v)
            public void onClick(View v) {
                /*String temp = items.get(v.getTag().hashCode()).getNom();
                Intent viewRecepta = new Intent(getApplicationContext(), ViewRecepta.class).putExtra("NomRecepta", temp).putExtra("direct", true);
                startActivity(viewRecepta);
                finish();*/
            }
        });

        recycler.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.i("DEBUG","item selected");
        return NavigationController.onItemSelected(item.getItemId(),this);
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
}
