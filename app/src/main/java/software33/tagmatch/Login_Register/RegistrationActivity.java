package software33.tagmatch.Login_Register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import software33.tagmatch.R;
import software33.tagmatch.ServerConnection.TagMatchPostAsyncTask;
import software33.tagmatch.Utils.Constants;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextView registrationTitle = (TextView) findViewById(R.id.registration_title);
        /* MÈTODE PER FER SERVIR FONTS EXTERNES*/
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-BoldItalic.ttf");
        registrationTitle.setTypeface(face);
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

    public void validateUser(View view) {
        String user = ((EditText) findViewById(R.id.registrationUsername)).getText().toString();
        String pass = ((EditText) findViewById(R.id.registrationPassword)).getText().toString();
        String email = ((EditText) findViewById(R.id.registrationMail)).getText().toString();

        if(user.isEmpty()) {
            showError(getString(R.string.registration_wrong_username_void));
            return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showError(getString(R.string.registration_wrong_mail_match));
            return;
        }
        if (!validatePassword()) {
            return;
        }

        continueRegistration(email, user, pass);
    }

    public void backToLogin(View view){
        Intent act = new Intent(this, Login.class);
        startActivity(act);
        finish();
    }

    private void continueRegistration(String email, String username, String passw){
        Intent act = new Intent(this, RegistrationActivity2.class);
        act.putExtra("email", email);
        act.putExtra("username", username);
        act.putExtra("password", passw);
        startActivity(act);
    }

    private boolean validatePassword() {
        String pass1 = ((EditText) findViewById(R.id.registrationPassword)).getText().toString();
        String pass2 = ((EditText) findViewById(R.id.registrationPasswordConfirm)).getText().toString();
        if(pass1.isEmpty() || pass2.isEmpty()){
            showError(getString(R.string.registration_wrong_mail_match));
            return false;
        }
        if(!pass1.equals(pass2)){
            showError(getString(R.string.registration_wrong_password_missmatch));
            return false;
        }
        return true;
    }

    private void showError (String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return;
    }

    @Override
    public void onBackPressed() {
        Intent success = new Intent(this, Login.class); //FAlta guardar en algun puesto l'usuari
        startActivity(success);
    }
}
