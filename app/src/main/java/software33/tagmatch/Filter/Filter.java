package software33.tagmatch.Filter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ecommerce.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetTrendingAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class Filter extends AppCompatActivity implements View.OnClickListener {

    private ImageView search_button;
    private String city;
    private TableLayout tl;
    private ArrayList<String> suggestions;
    private AutoCompleteTextView sugg_hashtags;
    private ListView con_search;
    private ArrayList<String> hash_search;
    private ArrayAdapter<String> adapter;

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

        tl = (TableLayout) findViewById(R.id.filter_table);
        sugg_hashtags = (AutoCompleteTextView) findViewById(R.id.filter_search_field);
        con_search = (ListView) findViewById(R.id.listView);
        hash_search = new ArrayList<>();

        //PER EVITAR SCROLL
        con_search.setOnTouchListener(new ListView.OnTouchListener()
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

        con_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String pressed = hash_search.get(position);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                hash_search.remove(pressed);
                                Toast.makeText(getApplicationContext(),R.string.succ_delete,Toast.LENGTH_LONG).show();
                                con_search.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, hash_search));
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Filter.this, R.style.myDialog));
                builder.setMessage(getResources().getString(R.string.delete_hash)).setPositiveButton(R.string.positive_button, dialogClickListener).setNegativeButton(R.string.negative_button, dialogClickListener);
                builder.show();
            }
        });

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
                        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown,suggestions);
                        sugg_hashtags.setTextColor(Color.BLACK);
                        sugg_hashtags.setAdapter(adapter);
                        sugg_hashtags.setThreshold(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(jObject);
        /*PETICIO HASHTAGS*/
        getUserCity();

        sugg_hashtags.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    hash_search.add(sugg_hashtags.getText().toString());
                    sugg_hashtags.setText("");
                    con_search.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.dropdown, hash_search));
                    return true;
                }
                return false;
            }
        });

        fillCategories();
    }

    private void makeURL() {
        String url = Constants.IP_SERVER + "/ads/search?limit=" + Constants.SERVER_limitAdverts + "&idGreaterThan=" + Constants.SERVER_IdGreaterThan;

        String tags = null;
        for (String s : hash_search)
        {
            tags += s + "";
        }
        if(!hash_search.isEmpty()){
            clearHashTag(hash_search);
            for (int i=0; i<hash_search.size(); ++i) {
                url += "&hashtag=" + hash_search.get(i);
            }
        }

        for(int i = 0; i < tl.getChildCount(); i++){
            CheckBox cb = (CheckBox) ((TableRow) tl.getChildAt(i)).getChildAt(0);
            if(cb.isChecked()){
                url += "&category=" + cb.getText();
            }
        }

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

    private void fillCategories() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());
            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/categories", this){
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if (jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error, getApplicationContext());
                        } else {
                            JSONArray array = jsonObject.getJSONArray("arrayResponse");
                            for(int i = 0; i < array.length(); i++){
                                TableRow row = new TableRow(getApplicationContext());
                                CheckBox cb = new CheckBox(getApplicationContext());
                                cb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                                cb.setText(array.getString(i));
                                cb.setTextColor(getResources().getColor(android.R.color.black));
                                row.addView(cb,0);
                                tl.addView(row,i);
                            }
                        }
                    } catch (Exception ignored){}
                }
            }.execute(jsonObject);
        } catch (Exception ignored) {}

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

    private void clearHashTag(ArrayList<String> search){
        for(int i=0; i<search.size(); ++i){
            if(search.get(i).startsWith("#")){
                search.set(i, search.get(i).replaceFirst("#", ""));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent novaRecepta = new Intent(getApplicationContext(), Home.class);
        startActivity(novaRecepta);
        finish();
    }

    private String cleanJSON(String input) {
        input = input.replaceAll("\\[","");
        input = input.replaceAll("\\]","");
        input = input.replaceAll("\"","");
        return input;
    }

}
