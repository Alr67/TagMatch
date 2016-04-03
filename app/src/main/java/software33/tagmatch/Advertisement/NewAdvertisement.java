package software33.tagmatch.Advertisement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import software33.tagmatch.Domain.AdvGift;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.Constants;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;

public class NewAdvertisement extends AppCompatActivity implements View.OnClickListener {

    private Spinner categorySpinner, typeSpinner;
    private Button createButton;
    private EditText title,description, tag;
    private List<Image> images;

    private boolean primera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisement);
        primera = true;
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
        Log.v("DEBUG NewAdvert", "I've just initialized createButton");
        createButton.setOnClickListener(this);

        title = (EditText) findViewById(R.id.input_title);
        description = (EditText) findViewById(R.id.input_description);
        tag  = (EditText) findViewById(R.id.input_hashtags);

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newImage:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Constants.codeImagePicker);
                break;
            case R.id.btn_newAdvert:
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
                break;
        }
    }


}
