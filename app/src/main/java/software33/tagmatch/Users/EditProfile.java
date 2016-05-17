package software33.tagmatch.Users;

import android.Manifest;
import software33.tagmatch.Chat.FirebaseUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetImageAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetTrendingAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPostImgAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.BitmapWorkerTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class EditProfile extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LatLng userPosition;
    private ListView interests_hash;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private ImageView iv;
    private TextView title, user_loc_text;
    private boolean imgMod, locationMod;
    private String imgExtension;
    private ArrayList<String> listdata, suggestions;
    private AutoCompleteTextView sugg_hashtags;
    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_profile);
        fab.setVisibility(View.GONE);

        imgMod = locationMod = false;

        listdata = new ArrayList<>();
        iv = (ImageView) findViewById(R.id.edit_profile_imageView);

        myId = FirebaseUtils.getMyId(this);

        Bundle extras = getIntent().getExtras();

        user_loc_text = (TextView) findViewById(R.id.textView13);
        user_loc_text.setText(extras.getString("city"));

        title = (TextView) findViewById(R.id.toolbar_title_edit_prof);
        title.setText(getResources().getString(R.string.ed_1) + extras.getString("username") + getResources().getString(R.string.ed_2));
        if (Build.VERSION.SDK_INT < 23) {
            title.setTextAppearance(getApplicationContext(),R.style.normalText);
        } else {
            title.setTextAppearance(R.style.normalText);
        }

        userPosition = (LatLng) extras.get("userPosition");

        sugg_hashtags = (AutoCompleteTextView) findViewById(R.id.sugg_hashtag_edit_prof);

        /*PETICIO HASHTAGS*/
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA amb Json");
            e.printStackTrace();
        }
        new TagMatchGetTrendingAsyncTask(Constants.IP_SERVER + "/tags/trending", getApplicationContext()) {
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    if (jsonObject.has("status"))
                        Log.i(Constants.DebugTAG, "status: " + jsonObject.getInt("status"));
                    Log.i(Constants.DebugTAG, "JSON: \n" + jsonObject);
                    if (jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                    } else {
                        String add = jsonObject.getString("200");
                        add = Helpers.cleanJSON(add);
                        suggestions = new ArrayList<String>(Arrays.asList(add.split(",")));
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown,suggestions);
                        sugg_hashtags.setTextColor(Color.BLACK);
                        sugg_hashtags.setAdapter(adapter);
                        sugg_hashtags.setThreshold(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(jObject);

        interests_hash = (ListView) findViewById(R.id.profile_interests_edit);
        /*PETICIO HASHTAGS*/

        sugg_hashtags.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    listdata.add(sugg_hashtags.getText().toString());
                    sugg_hashtags.setText("");
                    interests_hash.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, listdata));
                    return true;
                }
                return false;
            }
        });


        //PER EVITAR SCROLL
        interests_hash.setOnTouchListener(new ListView.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        interests_hash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String pressed = listdata.get(position);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                listdata.remove(pressed);
                                Toast.makeText(getApplicationContext(),R.string.succ_delete,Toast.LENGTH_LONG).show();
                                interests_hash.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, listdata));
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(EditProfile.this, R.style.myDialog));
                builder.setMessage(getResources().getString(R.string.delete_hash)).setPositiveButton(R.string.positive_button, dialogClickListener).setNegativeButton(R.string.negative_button, dialogClickListener);
                builder.show();
            }
        });


        fillListView();

        //para que no se abra el teclado al entrar en la activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        title = (TextView) findViewById(R.id.toolbar_title_edit_prof);
        //title.setText(getResources().getString(R.string.ed_1) + user.getAlias() + getResources().getString(R.string.ed_2));

        initMap();
        getIMGFromServer();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_prof, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            if(imgMod) updateIMG();
            updateLocInterest();
            Intent intent = new Intent(this, ViewProfile.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillListView() {
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
                            listdata = new ArrayList<String>();
                            if (interestsArray != null) {
                                for (int i=0;i<interestsArray.length();i++){
                                    listdata.add(interestsArray.get(i).toString());
                                }
                            }
                            interests_hash.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, listdata));
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
        String[] stockArr = new String[listdata.size()];
        stockArr = listdata.toArray(stockArr);
        return stockArr;
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
        try {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.edit_profile_map)).getMap();


                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
                map.addMarker(new MarkerOptions().position(userPosition));


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
        } catch (Exception ignored) {}

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


    private void updateIMG() {
        try {
            iv.setDrawingCacheEnabled(true);
            iv.buildDrawingCache();
            Bitmap bm = iv.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] byteArray = stream.toByteArray();

            JSONObject jObject = new JSONObject();
            jObject.put("profilePhotoId", byteArray);

            new TagMatchPostImgAsyncTask(Constants.IP_SERVER + "/users/photo", this, byteArray, imgExtension) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        showError(error);
                    } catch (JSONException ignored) {
                        Map<String, Object> img = new HashMap<>();
                        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        img.put("img",encodedImage);
                        FirebaseUtils.getUsersRef().child(myId).updateChildren(img);
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
        Intent intent = new Intent(this, ViewProfile.class);
        startActivity(intent);
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
