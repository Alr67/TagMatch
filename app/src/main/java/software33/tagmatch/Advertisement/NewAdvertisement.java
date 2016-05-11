package software33.tagmatch.Advertisement;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.AdvChange;
import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.AdvSell;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetBitmapAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetTrendingAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPostAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPostImgAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.DialogError;
import software33.tagmatch.Utils.Helpers;

public class NewAdvertisement extends AppCompatActivity implements View.OnClickListener {

    private Button createButton, newImage;
    private EditText title,description, tag, wantedTags;
    private TextInputLayout wantedTagsLayout;
    private CustomPagerAdapterNewAdvert mCustomPagerAdapterNewAdvert;
    private ViewPager mViewPager;
    private String imgExtension;
    private final String DebugTag = "DEBUG ADVERT";
    private Advertisement adv;
    private boolean edit = false;
    private Intent intent;
    private Integer okImages;
    private AutoCompleteTextView sugg_hashtags, sugg_hashtags_ex;
    private ArrayList<String> suggestions;
    private List<Bitmap> images;
    private String pathfoto;
    private Spinner categorySpinner, typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisement);
        initElements();

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initElements(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_advert);
        setSupportActionBar(toolbar);

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, Constants.typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new CustomSpinnerOnItemSelectedListener(this));

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Constants.categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new CustomSpinnerOnItemSelectedListener(this));

        createButton = (Button) findViewById(R.id.btn_newAdvert);
        createButton.setOnClickListener(this);

        newImage = (Button) findViewById(R.id.newImage);
        newImage.setOnClickListener(this);

        wantedTagsLayout = (TextInputLayout) findViewById(R.id.input_price_textInput);
        title = (EditText) findViewById(R.id.input_title);
        description = (EditText) findViewById(R.id.input_description);
        tag  = (EditText) findViewById(R.id.input_hashtags);
        tag.setClickable(false);
        wantedTags  = (EditText) findViewById(R.id.input_price);
        //wantedTags.setInputType(InputType.TYPE_CLASS_NUMBER);

        images = new ArrayList<>();

        sugg_hashtags = (AutoCompleteTextView) findViewById(R.id.sugg_hashtag);
        sugg_hashtags_ex = (AutoCompleteTextView) findViewById(R.id.sugg_hashtag_ex);
        sugg_hashtags_ex.setVisibility(View.GONE);

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
                        add = cleanJSON(add);
                        suggestions = new ArrayList<String>(Arrays.asList(add.split(",")));
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown,suggestions);
                        sugg_hashtags.setTextColor(Color.BLACK);
                        sugg_hashtags.setAdapter(adapter);
                        sugg_hashtags.setThreshold(1);
                        sugg_hashtags_ex.setTextColor(Color.BLACK);
                        sugg_hashtags_ex.setAdapter(adapter);
                        sugg_hashtags_ex.setThreshold(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(jObject);
        /*PETICIO HASHTAGS*/

        sugg_hashtags.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String aux = tag.getText().toString();
                    aux += sugg_hashtags.getText().toString() + " ";
                    tag.setText(aux);
                    sugg_hashtags.setText("");
                    return true;
                }
                return false;
            }
        });

        sugg_hashtags_ex.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String aux = tag.getText().toString();
                    aux += sugg_hashtags_ex.getText().toString() + " ";
                    wantedTags.setText(aux);
                    sugg_hashtags_ex.setText("");
                    return true;
                }
                return false;
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceHeight = displayMetrics.heightPixels;
        // Changes the height and width to the specified *pixels*
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = deviceHeight/3;
        mViewPager.setLayoutParams(params);
        mCustomPagerAdapterNewAdvert = new CustomPagerAdapterNewAdvert(this,params.height,params.width);
        mViewPager.setAdapter(mCustomPagerAdapterNewAdvert);


         intent = getIntent();

        if(intent.hasExtra("edit") && intent.getBooleanExtra("edit",false)) { //Si es edit
            createButton.setText(R.string.text_edit_button);
            edit = true;
            getAdvertisement(intent.getIntExtra("idAnunci",0));
        }
        else setTitle(R.string.new_ad_general_title);

    }

    private String cleanJSON(String input) {
        input = input.replaceAll("\\[","");
        input = input.replaceAll("\\]","");
        input = input.replaceAll("\"","");
        return input;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newImage:
               onNewImageClicked();
                break;
            case R.id.btn_newAdvert:
                onAdvertCreateClicked();
                break;
            default:
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                break;

        }
    }

    public void showBasicErrorMessage(String message) {
        FragmentTransaction frag = getFragmentManager().beginTransaction();
        DialogFragment dialogFragment = DialogError.newInstance(message, getString(R.string.ok),getString(R.string.title_error_newAdvert));
        dialogFragment.show(frag, "ShowErrorMessage");
    }

    public void onAdvertCreateClicked() {
        if(title.getText().toString().replaceAll("\\s","") == "") {
           showBasicErrorMessage(getResources().getString(R.string.noTitle));
        }
        else if(description.getText().toString().replaceAll("\\s","") == "") {
            showBasicErrorMessage(getResources().getString(R.string.noDescription));
        }
        else if(tag.getText().toString().replaceAll("\\s","") == "") {
            showBasicErrorMessage(getResources().getString(R.string.noTag));
        }
        else {
            String tagLine = tag.getText().toString().replace("#","");
            String[] tags = tagLine.split(" ");
            Advertisement adv = new Advertisement();
            Log.v(DebugTag,"Tipus seleccionat: " + typeSpinner.getSelectedItem());
            //TODO sujeto a cambios cuando implementemos los usuarios
            User owner = Helpers.getActualUser(this);

            if(typeSpinner.getSelectedItem().equals(Constants.typeGift)) {
                //owner, title, images, desc, tags, category
                adv = new AdvGift(owner,title.getText().toString(),images,description.getText().toString(),tags,categorySpinner.getSelectedItem().toString());
            }
            else if(typeSpinner.getSelectedItem().equals(Constants.typeExchange)) {
                String wantedTagLine = wantedTags.getText().toString().replace("#","");
                String[] wantedTag =  wantedTagLine.split(" ");
                adv = new AdvChange(owner,title.getText().toString(),images,description.getText().toString(),tags,categorySpinner.getSelectedItem().toString(),wantedTag);
            }
            else if(typeSpinner.getSelectedItem().equals(Constants.typeSell)) {
                String text = wantedTags.getText().toString();
                if(!text.equals("")) {
                    Double price = Double.parseDouble(text);
                    if(price <= 0.0) showBasicErrorMessage(getResources().getString(R.string.zeroPrice));
                    else {
                        Log.v(DebugTag, "Price: " + price);
                        adv = new AdvSell(owner, title.getText().toString(), images, description.getText().toString(), tags, categorySpinner.getSelectedItem().toString(), price);
                    }
                }
                else showBasicErrorMessage(getResources().getString(R.string.emptyPrice));
            }
            else {
                Toast.makeText(this,"ERROR DE TIPUUUUUS", Toast.LENGTH_SHORT).show();
            }
            updateAdvertToServer(adv);
        }
    }

    private void updateAdvertToServer(Advertisement adv) {
        final Context context = this.getApplicationContext();
        Toast.makeText(this,"Uploading Advert To server, please whait",Toast.LENGTH_LONG).show();
        if(edit) {
            //System.out.println("VAMOSSS: " +Constants.IP_SERVER + "/ads/" + intent.getIntExtra("idAnunci",0));
            new TagMatchPutAsyncTask(Constants.IP_SERVER + "/ads/" + intent.getIntExtra("idAnunci",0), getApplicationContext()){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if (jsonObject.has("status"))  Log.i(Constants.DebugTAG,"status: "+jsonObject.getInt("status"));
                        Log.i(Constants.DebugTAG,"JSON: \n"+jsonObject);
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                        }
                        else {
                            if(jsonObject.has("id")) {
                                Log.i(Constants.DebugTAG,"Advert data updated to server, proceed to update image");
                                postImagesToServer(jsonObject.getInt("id"));
                            }
                        }
                    } catch (JSONException ignored){

                    }
                }
            }.execute(adv.toJSON2());
            Log.i(Constants.DebugTAG, adv.toJSON().toString());
        }
        else {
            Toast.makeText(this,"Uploading Advert To server, please whait",Toast.LENGTH_LONG).show();
            new TagMatchPostAsyncTask(Constants.IP_SERVER + "/ads", this, true){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        Log.i(Constants.DebugTAG,"JSON: \n"+jsonObject);
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(jsonObject.has("id")) {
                                Log.i(Constants.DebugTAG,"Advert data updated to server, proceed to update image");
                                postImagesToServer(jsonObject.getInt("id"));
                            }
                        }
                    } catch (JSONException ignored){

                    }
                }
            }.execute(adv.toJSON());
        }
        Log.i(Constants.DebugTAG, adv.toJSON().toString());
    }

    private void postImagesToServer(Integer advId) {
        okImages = 0;
        if(images.size() == 0) {
            Log.i(Constants.DebugTAG, "Aquest anunci no te imatges");
            advertUpdated();
        }
        else {
            Log.i(Constants.DebugTAG,"Aquest anunci te " + images.size() + " fotos, anem a pujarles al server");

            String url = Constants.IP_SERVER + "/ads/" + advId + "/photos";
            for (Bitmap bm : images) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                JSONObject jObject = new JSONObject();
                try {
                    jObject.put("profilePhotoId", byteArray);
                    new TagMatchPostImgAsyncTask(url, this, byteArray, imgExtension) {
                        @Override
                        protected void onPostExecute(JSONObject jsonObject) {
                            try {
                                if (jsonObject.has("error")) {
                                    String error = jsonObject.get("error").toString();
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                } else {
                                    newImageUpdated();
                                }
                            } catch (JSONException ignored) {
                                ignored.printStackTrace();
                            }
                        }
                    }.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void newImageUpdated(){
        ++okImages;
        if(okImages == images.size()) {
            advertUpdated();
        }
    }

    private void advertUpdated() {
        if(edit) Toast.makeText(getApplicationContext(), "Congratulations, advertisement updated", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Congratulations, advertisement created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
        finish();
    }

    /** MARK: Spinner type manager*/
    public void onTypeSpinnerChanged(String newType) {
        if(newType.equals(Constants.typeGift)) {
            wantedTagsLayout.setHint(getString(R.string.input_gift));
            wantedTags.setEnabled(false);
            sugg_hashtags_ex.setVisibility(View.GONE);
            wantedTags.setText("");
        }
        else if(newType.equals(Constants.typeExchange)) {
            wantedTagsLayout.setHint(getString(R.string.input_wantedHashtags));
            wantedTags.setEnabled(false);
            wantedTags.setClickable(false);
            sugg_hashtags_ex.setVisibility(View.VISIBLE);
            wantedTags.setText("");
            wantedTags.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else if(newType.equals(Constants.typeSell)) {
            wantedTagsLayout.setHint(getString(R.string.input_price));
            wantedTags.setEnabled(true);
            sugg_hashtags_ex.setVisibility(View.GONE);
            wantedTags.setText("");
            wantedTags.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

 /** MARK: IMAGES */
    public void onNewImageClicked() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.imagePickTitle));
        builder.setMessage(getResources().getString(R.string.imagePickText));
        builder.setNeutralButton(getResources().getString(R.string.imagePickGallery), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                galleryPick();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.imagePickCamera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraPick();
            }
        });
        builder.show();
    }

    public void galleryPick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       // photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.codeImagePicker);
    }

    public void cameraPick() {
        Log.v(DebugTag,"Vaig al cameraPicker");
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
        else Log.e(DebugTag,"Error en crear la foto");
    }

    private File createImageFile() throws IOException {
        Log.v(DebugTag, "Anem a crear el fitxer de la foto");
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

    public String getImagePathFromUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        Log.i(DebugTag, picturePath);
        cursor.close();

        String selectedPic = picturePath;

        String[] parts = selectedPic.split("\\.");
        imgExtension = parts[parts.length - 1];
        Log.i(DebugTag, imgExtension);
        return selectedPic;
    }

    public void newImageConverted(Bitmap bitmap) {
        images.add(bitmap);
    }

 /** MARK: ACTIVITY RESULT**/
    protected void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) {
        switch(requestCode) {
            case Constants.codeImagePicker:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String selectedPic = getImagePathFromUri(selectedImage);
                    Log.i(Constants.DebugTAG,"Result gallery path: "+selectedPic);
                    mCustomPagerAdapterNewAdvert.addImage(selectedPic);
                }
                break;
            case Constants.codeCameraPicker:
                if(resultCode == RESULT_OK) {
                    Log.i(Constants.DebugTAG,"Result gallery path: "+pathfoto);
                    mCustomPagerAdapterNewAdvert.addImage(pathfoto);
                }
                break;
            default:
                Log.e(DebugTag, "Error, He entrat al default del onActivityResult");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(edit) {
            Intent viewRecepta = new Intent(getApplicationContext(), ViewAdvert.class).putExtra(Constants.TAG_BUNDLE_IDVIEWADVERTISEMENT, adv.getID());
            viewRecepta.putExtra(Constants.TAG_BUNDLE_USERVIEWADVERTISEMENT,adv.getOwner().toString());
            startActivity(viewRecepta);
            finish();
        }
        else {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }

    }


    //Part només de l'edit
    public void getAdvertisement(Integer id) {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        Log.i(Constants.DebugTAG, "Vaig a mostrar l'anunci amb id: " + id);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
            Log.i(Constants.DebugTAG,"Vaig a fer el get a: "+ Constants.IP_SERVER+"/ads/"+id.toString());
            new TagMatchGetAsyncTask(Constants.IP_SERVER+"/ads/"+id.toString(),this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    Log.i(Constants.DebugTAG,"onPostExecute");
                    Log.i(Constants.DebugTAG,"JSON: "+jsonObject.toString());
                    adv = Helpers.convertJSONToAdvertisement(jsonObject);
                    setTitle("Editing " +adv.getTitle());
                    fillComponents();
                    //String error = jsonObject.get("error").toString();
                }
            }.execute(jObject);
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA");
            e.printStackTrace();
        }
    }

    private void fillComponents() {
        getAdvertisementImages();
        if(adv.getTypeDescription().equals(Constants.typeServerEXCHANGE)) {
            sugg_hashtags_ex.setVisibility(View.VISIBLE);
            wantedTags.setText(((AdvChange)adv).getWantedTags().toString());
            wantedTags.setFocusable(true);
        }
        title.setText(adv.getTitle());
        description.setText(adv.getDescription());
        int type = Helpers.getIntFromType(adv.getTypeDescription());
        typeSpinner.setSelection(type);
        if(type == 0) wantedTags.setText(adv.getPrice().toString());
        categorySpinner.setSelection(Helpers.getIntFromCategory(adv.getCategory()));
        String sol = new String();
        for(int q = 0; q < adv.getTags().length; ++q) {
            sol += adv.getTags()[q] + " ";
        }
        tag.setText(sol);
        if(type == 1) {
            String next = new String();
            for(int s = 0; s < ((AdvChange) adv).getWantedTags().length; ++s) {
                next += ((AdvChange) adv).getWantedTags()[s];
            }
            wantedTags.setText(next);
        }
    }

    private void getAdvertisementImages() {
        JSONObject jObject = new JSONObject();
        User actualUser = Helpers.getActualUser(this);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());
        } catch (JSONException e) {
            Log.i(Constants.DebugTAG,"HA PETAT JAVA amb Json");
            e.printStackTrace();
        }
        String url = Constants.IP_SERVER+"/ads/"+ adv.getID().toString()+"/photo/";
        Log.i(Constants.DebugTAG,"Aquest anunci te "+adv.getImagesIDs().length+" fotos");
        for (String photoId :adv.getImagesIDs()) {
            Log.i(Constants.DebugTAG, "Vaig a demanar la foto amb id: " + photoId);

            new TagMatchGetBitmapAsyncTask(url+photoId.toString(),getApplicationContext()) {
                @Override
                protected void onPostExecute(Bitmap image) {
                    mCustomPagerAdapterNewAdvert.addImageBitmap(image);
                    Log.i(Constants.DebugTAG,"image added");
                    images.add(image);
                }
            }.execute(jObject);
        }
    }
}
