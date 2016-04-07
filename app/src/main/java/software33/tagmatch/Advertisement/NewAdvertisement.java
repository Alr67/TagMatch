package software33.tagmatch.Advertisement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.Utils.Constants;

public class NewAdvertisement extends AppCompatActivity implements View.OnClickListener {

    private Spinner categorySpinner, typeSpinner;
    private Button createButton, newImage;
    private EditText title,description, tag;
    private List<Bitmap> images;
    private CustomPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;
    private String imgExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisement);
        initElements();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void initElements(){
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, Constants.typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new CustomSpinnerOnItemSelectedListener());

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Constants.categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new CustomSpinnerOnItemSelectedListener());

        createButton = (Button) findViewById(R.id.btn_newAdvert);
        createButton.setOnClickListener(this);

        newImage = (Button) findViewById(R.id.newImage);
        newImage.setOnClickListener(this);

        title = (EditText) findViewById(R.id.input_title);
        description = (EditText) findViewById(R.id.input_description);
        tag  = (EditText) findViewById(R.id.input_hashtags);

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
        }
    }

    public void onAdvertCreateClicked() {
        if(title.getText().toString().replaceAll("\\s","") == "") {
            Toast.makeText(this, getResources().getString(R.string.noTitle), Toast.LENGTH_SHORT).show();
        }
        else if(description.getText().toString().replaceAll("\\s","") == "") {
            Toast.makeText(this, getResources().getString(R.string.noDescription), Toast.LENGTH_SHORT).show();
        }
        else if(tag.getText().toString().replaceAll("\\s","") == "") {
            Toast.makeText(this, getResources().getString(R.string.noTag), Toast.LENGTH_SHORT).show();
        }
        else {
            String[] tags = tag.getText().toString().split(" ");
            Advertisement adv;
            Log.v("DEBUG NewAdvert","Tipus seleccionat: " + typeSpinner.getSelectedItem());
            if(typeSpinner.getSelectedItem().equals(Constants.typeGift)) {
                //owner, title, images, desc, tags, category
                User owner = Constants.testUser;
                adv = new AdvGift(owner,title.getText().toString(),images,description.getText().toString(),tags,categorySpinner.getSelectedItem().toString());
                Toast.makeText(this,"Congratulations, advertisement created", Toast.LENGTH_SHORT).show();
            }
            else if(typeSpinner.getSelectedItem().equals(Constants.typeGift)) {

            }
            else if(typeSpinner.getSelectedItem().equals(Constants.typeGift)) {

            }
            else {
                Toast.makeText(this,"ERROR DE TIPUUUUUS", Toast.LENGTH_SHORT).show();
            }
            //TODO: send to server the new adv
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
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, Constants.codeCameraPicker);
    }

 /** MARK: ACTIVITY RESULT**/
    protected void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) {
        switch(requestCode) {
            case Constants.codeImagePicker:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

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
                    if (imgExtension.equals("jpg")) imgExtension = "jpeg";
                    Log.i("extension", imgExtension);

                    mCustomPagerAdapter.addImage(selectedPic);
                }
                break;
            case Constants.codeCameraPicker:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                /*    Drawable newImage = Drawable.createFromPath(selectedImage.getPath());
                    images.add(newImage);
                    mCustomPagerAdapter.addImage(newImage);
                    mCustomPagerAdapter.notifyAll();
*/
                }
                break;
            default:
                Log.e("Perfil", "Error, He entrat al default del onActivityResult");
                break;
        }
    }

/*

private File createImageFile() throws IOException {
        //Crea un nom unic per la foto
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //La guarda a la carpeta Pictures per defecte
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg",storageDir);
        pathfoto = image.getAbsolutePath();
        return image;
    }

@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.perfil_foto:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, 123);
                    }
                    else Log.e("Perfil","Error en crear la foto");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) {
        switch(requestCode) {
            case Constants.codeImagePicker:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    TaskFoto go = new TaskFoto();
                    go.execute(filePath);
                }
                break;
            case 123:
                if(resultCode == RESULT_OK) {
                    TaskFoto go = new TaskFoto();
                    go.execute(pathfoto);
                }
                break;
            default:
                Log.e("Perfil", "Error, He entrat al default del onActivityResult");
                break;
        }
    }



    private class TaskFoto extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... path) {
            if(!primera) {
                db = ioh.getWritableDatabase();
                if (db != null) {
                    String[] s = {};
                    db.execSQL("UPDATE TLogin SET photo='" + path[0] + "' WHERE username = '" + MenuPrincipal.nomUser + "'", s);
                }
                db.close();
            }
            else primera = false;
//Redimensionem la imatge
            int targetW = foto.getWidth();
            int targetH = foto.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path[0], bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            return BitmapFactory.decodeFile(path[0], bmOptions);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //   super.onPostExecute(bitmap);
            foto.setImageBitmap(bitmap);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    */
}
