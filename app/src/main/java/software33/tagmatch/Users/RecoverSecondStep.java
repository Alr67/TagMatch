package software33.tagmatch.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import software33.tagmatch.Login_Register.Login;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchPostAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchPutQRAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class RecoverSecondStep extends AppCompatActivity {

    @Bind(R.id.btn_last_step) Button recov;
    @Bind(R.id.input_password_rec1) EditText primera;
    @Bind(R.id.input_password_rec2) EditText segunda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_second_step);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_last_step)
    protected void end() {
        if(Helpers.isEmpty(primera) || Helpers.isEmpty(segunda)) {
            Toast.makeText(getApplicationContext(), R.string.error_empty_new_pass, Toast.LENGTH_SHORT).show();
        }
        else if(!primera.getText().toString().equals(segunda.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.both_must_be_equal, Toast.LENGTH_SHORT).show();
        }
        else {
            JSONObject jObject = new JSONObject();
            try {
                jObject.put("deviceToken", Helpers.getDeviceToken(getApplicationContext()));
                jObject.put("newPassword", primera.getText().toString());
                new TagMatchPutQRAsyncTask(getIntent().getStringExtra("RecoverURL"), this){
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        try {
                            Log.i(Constants.DebugTAG,"URL: \n"+Helpers.getDeviceToken(getApplicationContext()));
                            Log.i(Constants.DebugTAG,"URL: \n"+getIntent().getStringExtra("RecoverURL"));
                            Log.i(Constants.DebugTAG,"JSON: \n"+jsonObject);
                            if(jsonObject.has("error")) {
                                String error = jsonObject.get("error").toString();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                //Hacer q vaya al home
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                                Helpers.eraseDeviceToken(getApplicationContext());
                            }
                        } catch (JSONException ignored){

                        }
                    }
                }.execute(jObject);
            } catch (JSONException e) {
                Log.i(Constants.DebugTAG,"ERROR");
                e.printStackTrace();
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
