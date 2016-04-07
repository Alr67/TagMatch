package software33.tagmatch;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {
    /*Declaracions ButterKnife*/
    @Bind(R.id.btn_login) Button login;
    @Bind(R.id.link_signup) TextView reg;
    //@Bind(R.id.forg) Button forg;

    @Bind(R.id.input_email) EditText username;
    @Bind(R.id.input_password) EditText passw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this); //Necessari per a q funcioni tot lo de ButterKnife
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

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
            boolean haveError = false;

            try {
                JSONObject jObject = new JSONObject();
                jObject.put("username", name);
                jObject.put("password", pass);

                String direcc = getResources().getString(R.string.ip_server);
                direcc += "/users";

                new TagMatchGetAsyncTask(direcc) {
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            if(jsonObject.get("valid").toString().equals("false")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "MOLT BÉ", Toast.LENGTH_LONG).show();
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
    }

    /*@OnClick(R.id.forg)
    protected void intent_forg() {
        Toast.makeText(getApplicationContext(),username.getText().toString(),Toast.LENGTH_SHORT).show();
    }*/

    private void continueLogin() {
        Toast.makeText(getApplicationContext(),"MOLT BE, HAS FET LOGIN! :D", Toast.LENGTH_SHORT).show();
        /*Intent success = new Intent(this, Login.class); //FAlta guardar en algun puesto l'usuari
        startActivity(success);*/
    }

    @Override
    public void finish(){
        moveTaskToBack(true);
    }


}
