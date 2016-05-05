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

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import software33.tagmatch.Chat.FirebaseUtils;
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

        //para que no se abra el teclado al entrar en la activity
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

        //Try to create user in firebase
        Map<String, Object> img = new HashMap<>();
        img.put("img","");
        createUser(email, pass, user, img , this);

    }

    public void backToLogin(View view){
        Intent act = new Intent(this, Login.class);
        startActivity(act);
        finish();
    }

    private void continueRegistration(final String email, final String username, final String password) {
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("username", username);
            jObject.put("password", password);
            jObject.put("email", email);

            new TagMatchPostAsyncTask(Constants.IP_SERVER + "/users", this, false) {
                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    try {
                        String error = jsonObject.get("error").toString();
                        showError(error);
                        removeUser(email,password);

                    } catch (JSONException ignored) {
                        continueRegistration2(email, username, password);
                    }
                }
            }.execute(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void continueRegistration2(String email, String username, String password){
        Intent act = new Intent(this, RegistrationActivity2.class);
        act.putExtra("email", email);
        act.putExtra("username", username);
        act.putExtra("password", password);
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
        finish();
    }

    public void removeUser(final String email, final String password){
        FirebaseUtils.getMyFirebaseRef().removeUser(email, password, null);
    }

    public void createUser(final String email, final String password, final String name, final Map<String, Object> img, final Context context) {
        FirebaseUtils.getMyFirebaseRef().createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                FirebaseUtils.getMyFirebaseRef().authWithPassword(
                        email,
                        password,
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                FirebaseUtils.getUsersRef().child(authData.getUid()).setValue
                                        (new FirebaseUtils.User(name,"",new HashMap<String, Object>(),new HashMap<String, Object>()));
                                FirebaseUtils.setMyId(authData.getUid(),context);
                                FirebaseUtils.getUsersRef().child(FirebaseUtils.getMyId(context)).updateChildren(img);

                                continueRegistration(email, name, password);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError error) {
                                showError(error.getMessage());
                            }
                        }
                );
                //setMyId(result.get("uid").toString(), context);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                showError(firebaseError.getMessage());
            }
        });
    }
}
