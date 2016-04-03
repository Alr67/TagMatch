package software33.tagmatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class RegistrationActivity2 extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 8000;
    private static final int PICK_IMAGE = 8001;
    private ImageView iv;
    private String username;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LatLng userMarker;
    private String imgExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        iv = (ImageView) findViewById(R.id.imageView);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.registrationMap)).getMap();

        new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                userMarker = latLng;
                map.addMarker(new MarkerOptions().position(userMarker));
            }
        });

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

        //connectUser();

    }

    public void addPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        }
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
            Log.i("img path", picturePath);
            cursor.close();

            String selectedPic = picturePath;

            String[] parts = selectedPic.split("\\.");
            imgExtension = parts[parts.length - 1];

            if (imgExtension.equals("jpg"))
                imgExtension = "jpeg";

            Log.i("extension", imgExtension);

            BitmapWorkerTask task = new BitmapWorkerTask(iv);
            Integer hp, wp;
            hp = iv.getHeight();
            wp = iv.getWidth();
            task.execute(selectedPic, hp.toString(), wp.toString());

            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public void endRegistrer(View view) {
        if(imgExtension != null)
            updateIMG();
        if(userMarker != null)
            updateLocation();
        backToLogin();
    }

    private void updateIMG() {
        try {
            iv.setDrawingCacheEnabled(true);
            iv.buildDrawingCache();
            Bitmap bm = iv.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            JSONObject jObject = new JSONObject();
            jObject.put("profilePhotoId", byteArray);

            Log.i("updateImg", "he entrado");

            new TagMatchPostImgAsyncTask(getString(R.string.ip_server) + "/users/photo", this, byteArray, imgExtension) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        showError(error);
                    } catch (JSONException ignored) {
                    }
                }
            }.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateLocation() {
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("latitude", userMarker.latitude);
            jObject.put("longitude", userMarker.longitude);

            Log.i("updateLoc", "he entrado");

            new TagMatchPutAsyncTask(getString(R.string.ip_server) + "/users", this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        showError(error);
                    } catch (JSONException ignored) {
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void backToLogin() {
       /* Intent act = new Intent(this, Login.class);
        startActivity(act);    */
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RegistrationActivity2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://software33.tagmatch/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RegistrationActivity2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://software33.tagmatch/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location pos = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (pos != null) {
            map.clear();
            userMarker = new LatLng(pos.getLatitude(), pos.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 14.f));
            map.addMarker(new MarkerOptions().position(userMarker));
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

    private void showError(String msg) {
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }
}
