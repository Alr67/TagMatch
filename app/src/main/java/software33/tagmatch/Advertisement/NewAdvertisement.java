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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import software33.tagmatch.Domain.AdvChange;
import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.AdvSell;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchPostAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.DialogError;
import software33.tagmatch.Utils.NavigationController;

public class NewAdvertisement extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Button createButton, newImage;
    private EditText title,description, tag, wantedTags;
    private TextInputLayout wantedTagsLayout;
    private CustomPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;
    private String imgExtension;
    private final String DebugTag = "DEBUG ADVERT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_new_advert);
        initElements();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_advert);
        setSupportActionBar(toolbar);

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initElements(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_advert);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
      //  wantedTagsLayout.setHint(getString(R.string.input_price));

        title = (EditText) findViewById(R.id.input_title);
        description = (EditText) findViewById(R.id.input_description);
        tag  = (EditText) findViewById(R.id.input_hashtags);
        wantedTags  = (EditText) findViewById(R.id.input_price);
        wantedTags.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        images = new ArrayList<>();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceHeight = displayMetrics.heightPixels;
// Changes the height and width to the specified *pixels*
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = deviceHeight/3;
        mViewPager.setLayoutParams(params);
        mCustomPagerAdapter = new CustomPagerAdapter(this,params.height,params.width);
        mViewPager.setAdapter(mCustomPagerAdapter);

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
            String[] tags = tag.getText().toString().split(" ");
            Advertisement adv = new Advertisement();
            Log.v(DebugTag,"Tipus seleccionat: " + typeSpinner.getSelectedItem());

            //TODO sujeto a cambios cuando implementemos los usuarios
            User owner = new User(getSharedPreferences("TagMatch_pref", Context.MODE_PRIVATE).getString("name", ""));

            if(typeSpinner.getSelectedItem().equals(Constants.typeGift)) {
                //owner, title, images, desc, tags, category
                adv = new AdvGift(owner,title.getText().toString(),images,description.getText().toString(),tags,categorySpinner.getSelectedItem().toString());
                Toast.makeText(this,"Congratulations, advertisement created", Toast.LENGTH_SHORT).show();
            }
            else if(typeSpinner.getSelectedItem().equals(Constants.typeExchange)) {
                String[] wantedTag =  wantedTags.getText().toString().split(" ");
                adv = new AdvChange(owner,title.getText().toString(),images,description.getText().toString(),tags,categorySpinner.getSelectedItem().toString(),wantedTag);
                Toast.makeText(this,"Congratulations, advertisement created", Toast.LENGTH_SHORT).show();
            }
            else if(typeSpinner.getSelectedItem().equals(Constants.typeSell)) {
                String text = wantedTags.getText().toString();
                if(!text.equals("")) {
                    Double price = Double.parseDouble(text);
                    if(price <= 0.0) showBasicErrorMessage(getResources().getString(R.string.zeroPrice));
                    else {
                        Log.v(DebugTag, "Price: " + price);
                        adv = new AdvSell(owner, title.getText().toString(), images, description.getText().toString(), tags, categorySpinner.getSelectedItem().toString(), price);
                        Toast.makeText(this, "Congratulations, advertisement created", Toast.LENGTH_SHORT).show();
                    }
                }
                else showBasicErrorMessage(getResources().getString(R.string.emptyPrice));
            }
            else {
                Toast.makeText(this,"ERROR DE TIPUUUUUS", Toast.LENGTH_SHORT).show();
            }

            final Context context = this.getApplicationContext();

            new TagMatchPostAsyncTask(Constants.IP_SERVER + "/ads", this, true){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    } catch (JSONException ignored){
                        //TODO coger id para hacer el post imagen
                    }
                }
            }.execute(adv.toJSON());
            Log.i("jsonAdvert", adv.toJSON().toString());
            //TODO post para foto anuncio
        }
    }

 /** MARK: Spinner type manager*/
    private Spinner categorySpinner, typeSpinner;

    public void onTypeSpinnerChanged(String newType) {
        if(newType.equals(Constants.typeGift)) {
            wantedTagsLayout.setHint(getString(R.string.input_gift));
            wantedTags.setEnabled(false);
            wantedTags.setText("");
        }
        else if(newType.equals(Constants.typeExchange)) {
            wantedTagsLayout.setHint(getString(R.string.input_wantedHashtags));
            wantedTags.setEnabled(true);
            wantedTags.setText("");
            wantedTags.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else if(newType.equals(Constants.typeSell)) {
            wantedTagsLayout.setHint(getString(R.string.input_price));
            wantedTags.setEnabled(true);
            wantedTags.setText("");
            wantedTags.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

 /** MARK: IMAGES */
    private List<Bitmap> images;

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

    private String pathfoto;
    private File createImageFile() throws IOException {
        Log.v(DebugTag, "Anem a crear el fitxer de la foto");
        //Create an unique name for the new picture
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //We save it at the default picture folder
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg",storageDir);
        pathfoto = image.getAbsolutePath();
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
        if (imgExtension.equals("jpg")) imgExtension = "jpeg";
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
                    mCustomPagerAdapter.addImage(selectedPic);
                }
                break;
            case Constants.codeCameraPicker:
                if(resultCode == RESULT_OK) {
                    Log.v(DebugTag,"Path: "+pathfoto);
                    mCustomPagerAdapter.addImage(pathfoto);
                }
                break;
            default:
                Log.e(DebugTag, "Error, He entrat al default del onActivityResult");
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.i("DEBUG","item selected");
        return NavigationController.onItemSelected(item.getItemId(),this);
    }
}
