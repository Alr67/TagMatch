package software33.tagmatch.Login_Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import software33.tagmatch.Advertisement.NewAdvertisement;
import software33.tagmatch.MainActivity;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class Login extends AppCompatActivity {
    /*Declaracions ButterKnife*/
    @Bind(R.id.btn_login) Button login;
    @Bind(R.id.link_signup) TextView reg;
    //@Bind(R.id.forg) Button forg;

    @Bind(R.id.input_email) EditText username;
    @Bind(R.id.input_password) EditText passw;

    //DEVELOP
    @Bind(R.id.btn_debug_newAdvert) Button newAdvert;
    private static final String SH_PREF_NAME = "TagMatch_pref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this); //Necessari per a q funcioni tot lo de ButterKnife

        ArrayList<String> existing_login = new ArrayList<String>();
        existing_login = new Helpers().getPersonalData(getApplicationContext());
        if(existing_login != null && existing_login.get(0) != null && existing_login.get(1) != null) {
            Intent success = new Intent(this, MainActivity.class); //FAlta guardar en algun puesto l'usuari
            startActivity(success);
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

                /*String direcc = Constants.IP_SERVER;
                direcc += "/users";*/
                String direcc = "https://tagmatch.herokuapp.com/users";
                new TagMatchGetAsyncTask(direcc, getApplicationContext()) {
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            if(jsonObject.getInt("status") != 200) Toast.makeText(getApplicationContext(),R.string.error_login,Toast.LENGTH_LONG).show();
                            else {
                                if (jsonObject.getBoolean("valid")) {
                                    continueLogin();
                                } else {
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

    @OnClick(R.id.btn_debug_newAdvert)
    protected void intent_newAdvert() {
      //  Firebase.setAndroidContext(this);
        Intent intent = new Intent(this, NewAdvertisement.class);
        startActivity(intent);
        finish();
    }
    /*@OnClick(R.id.forg)
    protected void intent_forg() {
        Toast.makeText(getApplicationContext(),username.getText().toString(),Toast.LENGTH_SHORT).show();
    }*/

    private void continueLogin() {
        SharedPreferences.Editor editor = getSharedPreferences(SH_PREF_NAME, MODE_PRIVATE).edit();
        editor.putString("name", username.getText().toString()); //Fem l'acces dsd aqui perq aqui només s'entra si tot estava OK, aixi q no estarà mai buit
        editor.putString("password",passw.getText().toString());
        editor.commit();
        Intent success = new Intent(this, MainActivity.class); //FAlta guardar en algun puesto l'usuari
        startActivity(success);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
