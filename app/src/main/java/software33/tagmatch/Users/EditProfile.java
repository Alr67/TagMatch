package software33.tagmatch.Users;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPostImgAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.BitmapWorkerTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class EditProfile extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TableLayout tl;
    private int lastTableID;
    private View.OnClickListener btnOCL;
    private LatLng userPosition;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private ImageView iv;
    private boolean imgMod, locationMod;
    private String imgExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgMod)
                    updateIMG();
                updateLocInterest();
                backToProfile();
            }
        });

        imgMod = locationMod = false;

        Bundle extras = getIntent().getExtras();

        userPosition = (LatLng) extras.get("userPosition");

        lastTableID = 0;

        iv = (ImageView) findViewById(R.id.edit_profile_imageView);
        tl = (TableLayout) findViewById(R.id.edit_profile_table);

        btnOCL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowID = Integer.parseInt(((Button) v).getText().toString());
                tl.removeViewAt(rowID);
                refreshID();
            }
        };

        initTable();

        //para que no se abra el teclado al entrar en la activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initMap();
        getIMGFromServer();
    }

    private void initTable() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());

            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias(), this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error,getApplicationContext());
                        }
                        else if (jsonObject.has("username")){
                            JSONArray interestsArray = jsonObject.getJSONArray("interests");
                            for(int i = 0; i < interestsArray.length(); ++i) {
                                String interest = interestsArray.getString(i);
                                TextView tv = new TextView(getApplicationContext());
                                tv.setText(interest);
                                Button btn = new Button(getApplicationContext());
                                btn.setBackground(getDrawable(android.R.drawable.ic_menu_delete));
                                btn.setOnClickListener(btnOCL);
                                String sBtn = String.valueOf(lastTableID);
                                btn.setText(sBtn);
                                btn.setTextColor(getResources().getColor(android.R.color.transparent));
                                TableRow row = new TableRow(getApplicationContext());
                                row.addView(tv,0);
                                row.addView(btn,1);
                                tl.addView(row, lastTableID++);
                            }
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","error al get user");
                    }
                }
            }.execute(jsonObject);
        } catch (JSONException ignored) {}
    }

    private void updateLocInterest() {
        try {
            JSONObject jObject = new JSONObject();
            if(locationMod){
                jObject.put("latitude", userPosition.latitude);
                jObject.put("longitude", userPosition.longitude);
            }
            jObject.put("interests", new JSONArray(loadInterest(jObject)));
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/users", this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error, getApplicationContext());
                    } catch (JSONException ignored) {}
                }
            }.execute(jObject);
        } catch (Exception ignored) {}
    }

    private String[] loadInterest(JSONObject jObject) {
        String[] interests = new String[tl.getChildCount()];
        for(int i = 0; i < tl.getChildCount(); i++){
            TableRow row = (TableRow) tl.getChildAt(i);
            TextView tv = (TextView) row.getChildAt(0);
            interests[i] = tv.getText().toString();
        }
        return interests;
    }

    private void backToProfile() {
        Intent intent = new Intent(this, ViewProfile.class);
        startActivity(intent);
    }

    private void getIMGFromServer() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());

            new TagMatchGetImageAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias() + "/photo", this) {
                @Override
                protected void onPostExecute(String url) {
                    Picasso.with(EditProfile.this).load(url).error(R.drawable.image0).into(iv);
                    if (url == null){
                        Picasso.with(EditProfile.this).load(R.drawable.image0).into(iv);
                    }
                }
            }.execute(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initMap(){
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.edit_profile_map)).getMap();

        try {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
            map.addMarker(new MarkerOptions().position(userPosition));
        } catch (Exception ignored) {}

        new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                userPosition = latLng;
                map.addMarker(new MarkerOptions().position(userPosition));
                locationMod = true;
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
    }

    private void refreshID() {
        for(lastTableID = 0; lastTableID < tl.getChildCount(); lastTableID++){
            TableRow row = (TableRow) tl.getChildAt(lastTableID);
            Button btn = (Button) row.getChildAt(1);
            btn.setText(String.valueOf(lastTableID));
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

            JSONObject jObject = new JSONObject();
            jObject.put("profilePhotoId", byteArray);

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

    private void showError(String msg) {
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }

    public void addInterest(View view) {
        EditText et = (EditText) findViewById(R.id.edit_profile_et_interests);
        String interest = et.getText().toString();
        TextView tv = new TextView(this);
        tv.setText(interest);
        Button btn = new Button(this);
        btn.setBackground(getDrawable(android.R.drawable.ic_menu_delete));
        btn.setOnClickListener(btnOCL);
        String sBtn = String.valueOf(lastTableID);
        btn.setText(sBtn);
        btn.setTextColor(getResources().getColor(android.R.color.transparent));
        TableRow row = new TableRow(this);
        row.addView(tv,0);
        row.addView(btn,1);
        tl.addView(row, lastTableID++);
        et.setText("");
    }

    public void addPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.codeImagePicker);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.codeImagePicker && resultCode == RESULT_OK && null != data) {
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
            imgMod = true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("INFO", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("INFO", "GoogleApiClient connection has failed");
    }
}
