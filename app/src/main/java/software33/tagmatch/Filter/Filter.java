package software33.tagmatch.Filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class Filter extends AppCompatActivity implements View.OnClickListener {

    private ImageView search_button;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeURL();
            }
        });
        search_button = (ImageView) findViewById(R.id.search_image);
        search_button.setOnClickListener(this);

        getUserCity();
    }

    private void makeURL() {
        String url = Constants.IP_SERVER + "/ads/search?limit=" + Constants.SERVER_limitAdverts + "&idGreaterThan=" + Constants.SERVER_IdGreaterThan;

        String tags = ((EditText) findViewById(R.id.filter_search_field)).getText().toString();
        if(!tags.isEmpty()){
            String[] search = tags.split(" ");
            clearHashTag(search);
            for (int i=0; i<search.length; ++i) {
                url += "&hashtag=" + search[i];
            }
        }

        CheckBox cb_car = (CheckBox) findViewById(R.id.filter_cb_car);
        CheckBox cb_games = (CheckBox) findViewById(R.id.filter_cb_videogames);
        CheckBox cb_appliance = (CheckBox) findViewById(R.id.filter_cb_appliance);

        if(cb_car.isChecked())
            url += "category=Cotxes";

        if(cb_games.isChecked())
            url += "category=Videojocs";

        if(cb_appliance.isChecked())
            url += "category=Electrodomestics";

        CheckBox cb = (CheckBox) findViewById(R.id.filter_same_city);

        if(cb.isChecked()) {
            url += "&city=" + city;
        }

        backHome(url);
    }

    private void backHome(String url) {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.putExtra("previousActivity", "filter");
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
    }

    private void getUserCity() {
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
                            city = "";
                            showError(error);
                        }
                        else if (jsonObject.has("username")){
                            city = jsonObject.get("city").toString();
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","XD");
                    }
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
            }.execute(jsonObject);
        } catch (JSONException e) {
            city = "";
        }
    }

    private void showError(String error) {
        Context context = getApplicationContext();
        CharSequence text = error;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.search_image):
                Toast.makeText(getApplicationContext(), "Bro, try Android :)", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void clearHashTag(String[] search){
        for(int i=0; i<search.length; ++i){
            if(search[i].startsWith("#")){
                search[i] = search[i].replaceFirst("#", "");
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent novaRecepta = new Intent(getApplicationContext(), Home.class);
        startActivity(novaRecepta);
        finish();
    }
}
