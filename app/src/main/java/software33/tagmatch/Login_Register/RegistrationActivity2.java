package software33.tagmatch.Login_Register;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.Advertisement.CustomPagerAdapterNewAdvert;
import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchPostAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPostImgAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.BitmapWorkerTask;
import software33.tagmatch.Utils.CircleTransform;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class RegistrationActivity2 extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String SH_PREF_NAME = "TagMatch_pref";

    private ImageView iv;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LatLng userMarker;
    private String imgExtension;
    private ProgressDialog mDialog;
    private String email;
    private String username;
    private String password;
    private Map<String, Object> img = new HashMap<>();
    private String pathfoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        //CANVIAR COLOR DE LA STATUS BAR
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};

        getInfo(getIntent().getExtras());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {

                requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }

        iv = (ImageView) findViewById(R.id.imageView);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.registrationMap)).getMap();

        img.put("img","");

        try {
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
    }

    private void getInfo(Bundle bundle) {
        email = bundle.getString("email");
        username = bundle.getString("username");
        password = bundle.getString("password");
    }

    public void addPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.imagePickTitle));
            builder.setMessage(getResources().getString(R.string.imagePickText));
            builder.setNeutralButton(getResources().getString(R.string.imagePickGallery), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, Constants.codeImagePicker);
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.imagePickCamera), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.v("Register Error","Vaig al cameraPicker");
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePicture, Constants.codeCameraPicker);
                    }
                    else Log.e("Register Error", "Error en crear la foto");
                }
            });
            builder.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};
                requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
    }

    private File createImageFile() throws IOException {
        Log.v("Register Error", "Anem a crear el fitxer de la foto");
        //Create an unique name for the new picture
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //We save it at the default picture folder
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg",storageDir);
        pathfoto = image.getAbsolutePath();
        imgExtension = "jpg";
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case Constants.codeImagePicker:
                if(resultCode == RESULT_OK){
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
                break;
            case Constants.codeCameraPicker:
                if(resultCode == RESULT_OK) {
                    BitmapWorkerTask task = new BitmapWorkerTask(iv);
                    Integer hp, wp;
                    hp = iv.getHeight();
                    wp = iv.getWidth();
                    task.execute(pathfoto, hp.toString(), wp.toString());

                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                break;
            default:
                Log.e(Constants.DebugTAG, "Error, He entrat al default del onActivityResult");
                break;
        }
    }

    public void endRegistrer(View view) {
        if (imgExtension != null) {
            Helpers.setPersonalData(username, password, getApplicationContext());
            Log.i("REGISTRO",Helpers.getPersonalData(getApplicationContext()).get(0));
            updateIMG();
            /** Set img user in Firebase **/
            FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(this)).updateChildren(img);
            updateUser();
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.no_photo,Toast.LENGTH_LONG).show();
        }
    }

    private void updateIMG() {
        try {
            iv.setDrawingCacheEnabled(true);
            iv.buildDrawingCache();
            Bitmap bm = iv.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            /**** Update the image in firebase ****/
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            img.put("img",encodedImage);

            /*************************************/

            JSONObject jObject = new JSONObject();
            jObject.put("profilePhotoId", byteArray);

            Log.i("updateImg", "he entrado");

            new TagMatchPostImgAsyncTask(Constants.IP_SERVER + "/users/photo", this, byteArray, imgExtension) {
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

    private void updateUser() {
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("latitude", userMarker.latitude);
            jObject.put("longitude", userMarker.longitude);
            jObject.put("firebaseID", FirebaseUtils.getMyId(this));

            Log.i("updateLoc", "he entrado");

            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users", getApplicationContext()) {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mDialog = new ProgressDialog(RegistrationActivity2.this);
                    mDialog.setMessage(getString(R.string.creating_user));
                    mDialog.show();
                }
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        showError(error);
                    } catch (JSONException ignored) {
                        backToLogin();
                        mDialog.dismiss();
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void backToLogin() {
        Intent act = new Intent(this, Login.class);
        startActivity(act);
        finish();
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
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}; //ESTO ESTA AQUI PORQ SINO SE QUEJA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
        Location pos = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (pos != null) {
            map.clear();
            userMarker = new LatLng(pos.getLatitude(), pos.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 15));
            map.addMarker(new MarkerOptions().position(userMarker));
        } else {
            map.clear();
            userMarker = new LatLng(41.385064, 2.173403);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 15));
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
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        mDialog.dismiss();
    }
}

