package software33.tagmatch;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.io.IOException;

public class RegistrationActivity2 extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ImageView iv;
    String username;
    GoogleMap googleMap;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        Bundle intentData = getIntent().getExtras();

        iv = (ImageView) findViewById(R.id.imageView);
        //username = intentData.getString("username");

        initMap(savedInstanceState);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    private void initMap(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.registrationMap);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
    }

    public void addPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode==RESULT_OK){
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor c = getContentResolver().query(uri, projection, null, null, null);
                    c.moveToFirst();

                    int columnIndex = c.getColumnIndex(projection[0]);
                    String filePath = c.getString(columnIndex);
                    c.close();

                    Bitmap imageSelected = BitmapFactory.decodeFile(filePath);
                    Drawable d = new BitmapDrawable(imageSelected);

                    iv.setBackground(d);
                }
                break;
        }
    }

    public void endRegistrer(View view){
        //TO-DO
    }
}
