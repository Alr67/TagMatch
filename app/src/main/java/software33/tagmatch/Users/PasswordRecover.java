package software33.tagmatch.Users;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import software33.tagmatch.AdCards.Home;
import software33.tagmatch.Login_Register.Login;
import software33.tagmatch.Login_Register.RegistrationActivity;
import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchGetAsyncTask;
import software33.tagmatch.ServerConnection.TagMatchGetQRAsyncTask;
import software33.tagmatch.Utils.Constants;
import software33.tagmatch.Utils.Helpers;

public class PasswordRecover extends AppCompatActivity {

    @Bind(R.id.btn_recov) Button recov;
    @Bind(R.id.input_user_recov) EditText input_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);
        ButterKnife.bind(this);

        String[] permissions = {Manifest.permission.CAMERA};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }

    }

    @OnClick(R.id.btn_recov)
    protected void send_petition() {
        String name;

        if(!input_user.getText().toString().matches("") ) {
            name = input_user.getText().toString();

            JSONObject jObject = new JSONObject();
            //jObject.put("username", name);

            String direcc = Constants.IP_SERVER;
            direcc += "/users/"+name+"/recovery";

            new TagMatchGetQRAsyncTask(direcc, getApplicationContext()) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        if(jsonObject.has("status") && jsonObject.getInt("status") != 200) Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();

                        else {
                            if (jsonObject.has("deviceToken")) {
                                Log.i(Constants.DebugTAG,"URL: \n"+jsonObject.getString("deviceToken"));
                                Helpers.saveDeviceToken(getApplicationContext(),jsonObject.getString("deviceToken"));
                                IntentIntegrator scanIntegrator = new IntentIntegrator(PasswordRecover.this);
                                scanIntegrator.initiateScan();
                            } else {
                                //TODO cambiar el mensaje de error
                                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(jObject);

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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            Log.i(Constants.DebugTAG,"Toekn: \n"+Helpers.getDeviceToken(getApplicationContext()));
            //String scanFormat = scanningResult.getFormatName();
            Intent vamonos = new Intent(this, RecoverSecondStep.class);
            vamonos.putExtra("RecoverURL",scanContent);
            startActivity(vamonos);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
        }
    }

}
