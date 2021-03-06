package software33.tagmatch.Login_Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import software33.tagmatch.AdCards.AdvertContent;
import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Chat.FirebaseUtils;
import software33.tagmatch.Domain.Advertisement;
import software33.tagmatch.Domain.User;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetArrayAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Users.PasswordRecover;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class Login extends AppCompatActivity {
    /*Declaracions ButterKnife*/
    @Bind(R.id.btn_login) Button login;
    @Bind(R.id.link_signup) TextView reg;
    @Bind(R.id.link_forg) TextView forg;

    @Bind(R.id.input_email) EditText username;
    @Bind(R.id.input_password) EditText passw;

    private ProgressDialog mDialog;
    private static final String SH_PREF_NAME = "TagMatch_pref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this); //Necessari per a q funcioni tot lo de ButterKnife

        Firebase.setAndroidContext(this);

        ArrayList<String> existing_login = new ArrayList<String>();
        existing_login = new Helpers().getPersonalData(getApplicationContext());

        if(!existing_login.isEmpty() && existing_login.get(0) != null && existing_login.get(1) != null) {
            Intent success = new Intent(this, Home.class);
            startActivity(success);
            finish();
        }

        TextView app_header = (TextView) findViewById(R.id.app_header);
        /* MÈTODE PER FER SERVIR FONTS EXTERNES*/
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-BoldItalic.ttf");
        app_header.setTypeface(face);
        /* MÈTODE PER FER SERVIR FONTS EXTERNES*/

        //CANVIAR COLOR DE LA STATUS BAR
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @OnClick(R.id.btn_login)
    protected void validate_login()  {
        String name,pass;

        if(!username.getText().toString().matches("") && !passw.getText().toString().matches("")) {
            name = username.getText().toString();
            pass = passw.getText().toString();

            try {
                JSONObject jObject = new JSONObject();
                jObject.put("username", name);
                jObject.put("password", pass);

                String direcc = Constants.IP_SERVER;
                direcc += "/users";

                new TagMatchGetAsyncTask(direcc, getApplicationContext()) {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mDialog = new ProgressDialog(Login.this);
                        mDialog.setMessage(getString(R.string.loading));
                        mDialog.show();
                    }
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            if(jsonObject.has("status") && jsonObject.getInt("status") != 200) Toast.makeText(getApplicationContext(),R.string.error_login,Toast.LENGTH_LONG).show();
                            else {
                                if (jsonObject.has("valid") && jsonObject.getBoolean("valid")) {
                                    continueLogin();
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            if (username.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
            if (passw.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), R.string.empty_pass, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.link_signup)
    protected void intent_reg() {
        Firebase.setAndroidContext(this);
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.link_forg)
    protected void intent_forg() {
        Firebase.setAndroidContext(this);
        Intent intent = new Intent(this, PasswordRecover.class);
        startActivity(intent);
        finish();
    }

    private void continueLogin() {
        Firebase.setAndroidContext(this);

        //Guardem les dades de login
        Helpers.setPersonalData(username.getText().toString(), passw.getText().toString(), this);
        downloadUserFromServer();

    }

    private void endLogin() {
        mDialog.dismiss();
        Intent success = new Intent(this, Home.class);
        startActivity(success);
        finish();
    }

    private void downloadCategories() {
        JSONObject jObject = new JSONObject();
        Log.i(Constants.DebugTAG,"Lets get categories");
        User actualUser = Helpers.getActualUser(this);
        try {
            jObject.put("username", actualUser.getAlias());
            jObject.put("password", actualUser.getPassword());

        new TagMatchGetArrayAsyncTask(Constants.IP_SERVER + "/categories", this) {
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                try {
                    if(jsonObject.has("error")) {
                        String error = jsonObject.get("error").toString();
                        Helpers.showError(error,getApplicationContext());
                    }
                    else if(jsonObject.has("arrayResponse")) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("arrayResponse");
                            Constants.categoryList = new ArrayList<>();
                            for (int n = 0; n < jsonArray.length(); n++) {
                                Constants.categoryList.add(jsonArray.getString(n));
                            }
                            endLogin();
                            Log.i(Constants.DebugTAG,"Done downloading categories");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException ignored) {
                    Log.i("DEBUG","error al get user");
                }
            }
        }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadUserFromServer(){
        JSONObject jObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", Helpers.getActualUser(this).getAlias());
            jsonObject.put("password", Helpers.getActualUser(this).getPassword());
            new TagMatchGetAsyncTask(Constants.IP_SERVER + "/users/" + Helpers.getActualUser(this).getAlias().replaceAll(" ", "%20"), this) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("error")) {
                            String error = jsonObject.get("error").toString();
                            Helpers.showError(error,getApplicationContext());
                        }
                        else if (jsonObject.has("username")){
                            Log.i("Debug-GetUser",jsonObject.toString());
                            FirebaseUtils.setMyId(jsonObject.getString("username"),getApplicationContext());
                            endLogin();
                        }
                    } catch (JSONException ignored) {
                        Log.i("DEBUG","error al get user");
                    }
                }
            }.execute(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
