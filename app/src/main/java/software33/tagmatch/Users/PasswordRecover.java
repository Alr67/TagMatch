package software33.tagmatch.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import software33.tagmatch.Login_Register.Login;
import software33.tagmatch.Login_Register.RegistrationActivity;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class PasswordRecover extends AppCompatActivity {

    @Bind(R.id.btn_recov) Button recov;

    EditText input_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);
        ButterKnife.bind(this);


        input_user = (EditText) findViewById(R.id.input_user_recov);
    }

    @OnClick(R.id.btn_recov)
    protected void send_petition() {
        String name;

        if(!input_user.getText().toString().matches("") ) {
            name = input_user.getText().toString();

            try {
                JSONObject jObject = new JSONObject();
                jObject.put("username", name);

                String direcc = Constants.IP_SERVER;
                direcc += "/users/"+name+"/recovery";

                new TagMatchGetAsyncTask(direcc, getApplicationContext()) {
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            if(jsonObject.has("status") && jsonObject.getInt("status") != 200) Toast.makeText(getApplicationContext(),R.string.error_login,Toast.LENGTH_LONG).show();
                            else {
                                if (jsonObject.has("valid")) {
                                    Helpers.saveDeviceToken(getApplicationContext(),jsonObject.getInt("valid"));
                                    Toast.makeText(getApplicationContext(), "PROVANT " +jsonObject.getInt("valid"), Toast.LENGTH_LONG).show();
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
            if (input_user.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent success = new Intent(this, Login.class); //FAlta guardar en algun puesto l'usuari
        startActivity(success);
        finish();
    }

}
