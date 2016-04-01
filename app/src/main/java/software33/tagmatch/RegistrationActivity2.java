package software33.tagmatch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RegistrationActivity2 extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PICK_IMAGE = 8888;
    private static final int GPS_PERMISSION = 8000;
    private ImageView iv;
    private String username;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location userLocation;
    private static LatLng userMarker;
    private LocationManager locationManager;
    private Boolean ubicacionActiva;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Bundle intentData = getIntent().getExtras();

        iv = (ImageView) findViewById(R.id.imageView);
        //username = intentData.getString("username");
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.registrationMap)).getMap();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                userMarker = latLng;
                map.addMarker(new MarkerOptions().position(userMarker));
            }
        });


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){

        }
        else{

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.i("img path", picturePath + ".");
            cursor.close();
            String selectedPic = picturePath;


            BitmapWorkerTask task = new BitmapWorkerTask(iv);
            Integer hp, wp;
            hp = iv.getHeight();
            wp = iv.getWidth();
            task.execute(selectedPic, hp.toString(), wp.toString());

            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public void endRegistrer(View view) {
        //TO-DO
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location pos = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (pos != null) {
            map.clear();
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pos.getLatitude(), pos.getLongitude()), 14.f));
            map.addMarker(new MarkerOptions().position(new LatLng(pos.getLatitude(), pos.getLongitude())));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("INFO", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("INFO", "GoogleApiClient connection has failed");
    }



}

