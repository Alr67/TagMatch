package software33.tagmatch.Advertisement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.List;

import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.R;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;
import software33.tagmatch.Utils.NavigationController;

public class ViewAdvert extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener  {

    private Button chatButton,favouriteButton;
    private ImageView imageType, userImage;
    private List<Bitmap> images;
    private TextView title,description,tags,username,valoration;
    private ViewPager mViewPager;
    private CustomPagerAdapterViewAdvert mCustomPagerAdapterViewAdvert;
    Advertisement adv;

    public ViewAdvert(){
        adv = new Advertisement();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
      //  JSONObject json = i.getStringExtra(Constants.TAG_BUNDLE_VIEWADVERT);
        String st = i.getStringExtra(Constants.TAG_BUNDLE_VIEWADVERT);
        Log.i("DEBUG","JSON ADVERT: \n"+st);
        Helpers.getAdvertisementFromJSON(st);

        //   adv = (Advertisement) i.getSerializableExtra();
        setContentView(R.layout.nav_view_advert);
      //  initComponents();
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

        valoration = (TextView) findViewById(R.id.advert_title);
        valoration.setText(adv.getOwner().getValoration().toString());
        username = (TextView) findViewById(R.id.advert_title);
        username.setText(adv.getOwner().getAlias());
        tags = (TextView) findViewById(R.id.advert_title);
        tags.setText(adv.getTags().toString());
        description = (TextView) findViewById(R.id.advert_title);
        description.setText(adv.getDescription());
        title = (TextView) findViewById(R.id.advert_title);
        title.setText(adv.getTitle());
        favouriteButton = (Button) findViewById(R.id.advert_but_chat);
        favouriteButton.setOnClickListener(this);
        chatButton = (Button) findViewById(R.id.advert_but_chat);
        chatButton.setOnClickListener(this);

        prepareImages();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return  NavigationController.onItemSelected(item.getItemId(),this);
    }

    /** MARK: images */
    public void newImageConverted(Bitmap bitmap) {
        images.add(bitmap);
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
        favouriteButton.setBackground(getDrawable(R.drawable.advert_heart));

        imageType.getLayoutParams().height=params.height/5;
        imageType.getLayoutParams().width=params.height/5;
        if(adv.getTypeDescription().equals(Constants.typeServerEXCHANGE)) {
            imageType.setImageDrawable(getDrawable(R.drawable.advert_exchange));
        }
        else if(adv.getTypeDescription().equals(Constants.typeServerSELL)) {
            imageType.setImageDrawable(getDrawable(R.drawable.advert_sell));
        }
        else {
            imageType.setImageDrawable(getDrawable(R.drawable.advert_gift));
        }

        for(Bitmap image : adv.getImages()) {
            mCustomPagerAdapterViewAdvert.addImage(image);
        }
    }
}
